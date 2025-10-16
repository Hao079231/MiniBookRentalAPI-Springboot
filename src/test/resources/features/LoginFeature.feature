# language: vi
Tính năng: Kiểm thử API đăng nhập của hệ thống
  Mục tiêu: Xác minh API /api/token hoạt động đúng khi người dùng gửi thông tin đăng nhập

  Bối cảnh:
  Cho API "/api/token" đang sẵn sàng

  @Success
  Kịch bản: Đăng nhập thành công với tài khoản ADMIN hợp lệ
    Khi client gửi yêu cầu POST với nội dung JSON:
      """
      {
        "username": "admin",
        "password": "Admin123654@",
        "grantType": "admin"
      }
      """
    Thì API trả về mã trạng thái 200
    Và phản hồi chứa "Login success"
    Và phản hồi có trường "data.token"

  @Success
  Kịch bản: Đăng nhập thành công với tài khoản STAFF hợp lệ
    Khi client gửi yêu cầu POST với nội dung JSON:
      """
      {
        "email": "trinhtrunghao@gmail.com",
        "password": "Hao1234@",
        "grantType": "staff"
      }
      """
    Thì API trả về mã trạng thái 200
    Và phản hồi chứa "Login success"
    Và phản hồi có trường "data.token"

  @Fail
  Kịch bản: Đăng nhập thất bại với tài khoản STAFF do sai mật khẩu
    Khi client gửi yêu cầu POST với nội dung JSON:
      """
      {
        "email": "trinhtrunghao@gmail.com",
        "password": "SaiMatKhau123",
        "grantType": "staff"
      }
      """
    Thì API trả về mã trạng thái 400
    Và phản hồi chứa "Password invalid"

  @Fail
  Kịch bản: Đăng nhập thất bại với tài khoản STAFF không tồn tại
    Khi client gửi yêu cầu POST với nội dung JSON:
      """
      {
        "email": "congduyphu@gmail.com",
        "password": "Phu1234@",
        "grantType": "staff"
      }
      """
    Thì API trả về mã trạng thái 404
    Và phản hồi chứa "Account not found"

  @Fail
  Kịch bản: Đăng nhập thất bại với tài khoản STAFF thiếu email
    Khi client gửi yêu cầu POST với nội dung JSON:
      """
      {
        "email": "",
        "password": "Hao1234@",
        "grantType": "staff"
      }
      """
    Thì API trả về mã trạng thái 400
    Và phản hồi chứa "Email cannot be null"

  @Fail
  Kịch bản: Đăng nhập thất bại với tài khoản STAFF thiếu mật khẩu
    Khi client gửi yêu cầu POST với nội dung JSON:
      """
      {
        "email": "trinhtrunghao@gmail.com",
        "password": "",
        "grantType": "staff"
      }
      """
    Thì API trả về mã trạng thái 400
    Và phản hồi chứa "The password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number and one special character"

  @Fail
  Kịch bản: Đăng nhập thất bại với tài khoản STAFF thiếu trường grantType
    Khi client gửi yêu cầu POST với nội dung JSON:
      """
      {
        "email": "trinhtrunghao@gmail.com",
        "password": "Hao1234@"
      }
      """
    Thì API trả về mã trạng thái 400
    Và phản hồi chứa "Grant type cannot be null"

  @Fail
  Kịch bản: Đăng nhập thất bại với tài khoản STAFF có grantType sai
    Khi client gửi yêu cầu POST với nội dung JSON:
      """
      {
        "email": "trinhtrunghao@gmail.com",
        "password": "Hao1234@",
        "grantType": "user"
      }
      """
    Thì API trả về mã trạng thái 400
    Và phản hồi chứa "Invalid grant type"

  @Fail
  Kịch bản: Đăng nhập thất bại với tài khoản ADMIN do sai mật khẩu
    Khi client gửi yêu cầu POST với nội dung JSON:
      """
      {
        "username": "admin",
        "password": "SaiMatKhau123",
        "grantType": "admin"
      }
      """
    Thì API trả về mã trạng thái 400
    Và phản hồi chứa "Password invalid"

  @Fail
  Kịch bản: Đăng nhập thất bại với tài khoản ADMIN không tồn tại
    Khi client gửi yêu cầu POST với nội dung JSON:
      """
      {
        "username": "admin_fake",
        "password": "Admin123654@",
        "grantType": "admin"
      }
      """
    Thì API trả về mã trạng thái 404
    Và phản hồi chứa "Account not found"

  @Fail
  Kịch bản: Đăng nhập thất bại với tài khoản ADMIN thiếu username
    Khi client gửi yêu cầu POST với nội dung JSON:
      """
      {
        "username": "",
        "password": "Admin123654@",
        "grantType": "admin"
      }
      """
    Thì API trả về mã trạng thái 400
    Và phản hồi chứa "Username cannot be null"

  @Fail
  Kịch bản: Đăng nhập thất bại với tài khoản ADMIN thiếu mật khẩu
    Khi client gửi yêu cầu POST với nội dung JSON:
      """
      {
        "username": "admin",
        "password": "",
        "grantType": "admin"
      }
      """
    Thì API trả về mã trạng thái 400
    Và phản hồi chứa "The password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number and one special character"

  @Fail
  Kịch bản: Đăng nhập thất bại với tài khoản ADMIN thiếu trường grantType
    Khi client gửi yêu cầu POST với nội dung JSON:
      """
      {
        "username": "admin",
        "password": "Admin123654@"
      }
      """
    Thì API trả về mã trạng thái 400
    Và phản hồi chứa "Grant type cannot be null"

  @Fail
  Kịch bản: Đăng nhập thất bại với tài khoản ADMIN có grantType sai
    Khi client gửi yêu cầu POST với nội dung JSON:
      """
      {
        "username": "admin",
        "password": "Admin123654@",
        "grantType": "superadmin"
      }
      """
    Thì API trả về mã trạng thái 400
    Và phản hồi chứa "Invalid grant type"

  @Fail
  Kịch bản: Đăng nhập thất bại với tài khoản ADMIN khi gửi request rỗng
    Khi client gửi yêu cầu POST với nội dung JSON:
      """
      {}
      """
    Thì API trả về mã trạng thái 400
    Và phản hồi chứa "Missing required fields"
