package com.ute.rental.service.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.ute.rental.config.SecurityConstant;
import com.ute.rental.dto.ApiMessageDto;
import com.ute.rental.dto.AuthenticationDto;
import com.ute.rental.dto.ErrorCode;
import com.ute.rental.exception.BadRequestException;
import com.ute.rental.exception.NotFoundException;
import com.ute.rental.exception.UnauthorizationException;
import com.ute.rental.form.AuthenticationForm;
import com.ute.rental.model.Account;
import com.ute.rental.model.Group;
import com.ute.rental.model.InvalidatedToken;
import com.ute.rental.model.Permission;
import com.ute.rental.repository.AccountRepository;
import com.ute.rental.repository.InvalidatedTokenRepository;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@Slf4j
public class UserServiceImpl{
  @NonFinal
  @Value("${jwt.signerKey}")
  protected String SIGNER_KEY;

  @NonFinal
  @Value("${jwt.access-token-validity}")
  protected long VALID_DURATION;

  @NonFinal
  @Value("${jwt.refresh-token-validity}")
  protected long REFRESHABLE_DURATION;

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  InvalidatedTokenRepository invalidatedTokenRepository;

  public Boolean introspect(String token) throws JOSEException, ParseException {
    Boolean isValid = true;
    try {
      SignedJWT signedJWT = verifyToken(token, false);
    } catch (Exception e) {
      log.error("====> Introspect token false: " + e.getMessage());
      isValid = false;
    }

    return isValid;
  }

  public ApiMessageDto<AuthenticationDto> authenticate(AuthenticationForm request) {
    ApiMessageDto<AuthenticationDto> apiMessageDto = new ApiMessageDto<>();
    AuthenticationDto authenticationDto = new AuthenticationDto();
    if (Objects.equals(request.getGrantType(), SecurityConstant.ADMIN)){
      if (StringUtils.isBlank(request.getUsername())){
        throw new BadRequestException("Username cannot be null", ErrorCode.ACCOUNT_ERROR_USERNAME_NULL);
      }
      Account account = accountRepository.findByUsername(request.getUsername()).orElseThrow(()
          -> new NotFoundException("Account not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));
      PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

      boolean authenticated = passwordEncoder.matches(request.getPassword(), account.getPassword());

      if (!authenticated){
        throw new BadRequestException("Password invalid", ErrorCode.ACCOUNT_ERROR_PASSWORD);
      }
      authenticationDto.setToken(generateToken(account));
    } else if (Objects.equals(request.getGrantType(), SecurityConstant.STAFF)){
      if (StringUtils.isBlank(request.getEmail())){
        throw new BadRequestException("Email cannot be null", ErrorCode.ACCOUNT_ERROR_EMAIL_NULL);
      }
      Account account = accountRepository.findByEmail(request.getEmail()).orElseThrow(()
          -> new NotFoundException("Account not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));
      PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

      boolean authenticated = passwordEncoder.matches(request.getPassword(), account.getPassword());

      if (!authenticated){
        throw new BadRequestException("Password invalid", ErrorCode.ACCOUNT_ERROR_PASSWORD);
      }
      authenticationDto.setToken(generateToken(account));
    } else {
      throw new BadRequestException("Invalid grant type");
    }
    apiMessageDto.setData(authenticationDto);
    apiMessageDto.setMessage("Login success");
    return apiMessageDto;
  }

  private String generateToken(Account account) {
    JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

    JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
        .subject(account.getUsername())
        .issuer("minibook.com")
        .issueTime(new Date())
        .expirationTime(new Date(
            Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
        .jwtID(UUID.randomUUID().toString())
        .claim("scope", buildScope(account))
        .claim("id", account.getId())
        .claim("isAdmin", account.getIsAdmin())
        .build();

    Payload payload = new Payload(jwtClaimsSet.toJSONObject());

    JWSObject jwsObject = new JWSObject(header, payload);

    try {
      jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
      return jwsObject.serialize();
    } catch (JOSEException e) {
      log.error("Cannot create token", e);
      throw new RuntimeException(e);
    }
  }

  public ApiMessageDto<AuthenticationDto> refreshToken(String token) throws ParseException, JOSEException {
    ApiMessageDto<AuthenticationDto> apiMessageDto = new ApiMessageDto<>();
    AuthenticationDto authenticationDto = new AuthenticationDto();
    SignedJWT signedJWT = verifyToken(token, true);

    String jit = signedJWT.getJWTClaimsSet().getJWTID();
    var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

    InvalidatedToken invalidatedToken =
        InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

    invalidatedTokenRepository.save(invalidatedToken);

    String username = signedJWT.getJWTClaimsSet().getSubject();
    Account account = accountRepository.findByUsername(username).orElseThrow(() ->
        new NotFoundException("Username not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));
    authenticationDto.setToken(generateToken(account));
    apiMessageDto.setData(authenticationDto);
    apiMessageDto.setMessage("Refresh token success");
    return apiMessageDto;
  }

  private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
    JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

    SignedJWT signedJWT = SignedJWT.parse(token);

    Date expiryTime = (isRefresh)
        ? new Date(signedJWT
        .getJWTClaimsSet()
        .getIssueTime()
        .toInstant()
        .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
        .toEpochMilli())
        : signedJWT.getJWTClaimsSet().getExpirationTime();

    var verified = signedJWT.verify(verifier);

    if (!(verified && expiryTime.after(new Date()))) throw new UnauthorizationException("Token invalid", ErrorCode.TOKEN_ERROR_INVALID);

    if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
      throw new UnauthorizationException("Token invalid", ErrorCode.TOKEN_ERROR_INVALID);

    return signedJWT;
  }

  private List<String> buildScope(Account account) {
    List<String> scopes = new ArrayList<>();
    Group group = account.getGroup();
    if (!CollectionUtils.isEmpty(group.getPermissions())) {
      for (Permission permission : group.getPermissions()) {
        if (permission.getPermissionCode() != null) {
          scopes.add(permission.getPermissionCode());
        }
      }
    }
    return scopes;
  }
}
