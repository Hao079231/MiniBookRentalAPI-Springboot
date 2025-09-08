package com.ute.rental.service.id;

import com.ute.rental.model.ReuseId;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class idGenerator implements IdentifierGenerator {

  @Override
  public Object generate(SharedSessionContractImplementor sharedSessionContractImplementor,
      Object o) {
    try{
      ReuseId reuseId = (ReuseId) o;
      if (reuseId.getReuseId() != null){
        return reuseId.getReuseId();
      }
    } catch (RuntimeException e) {
      throw new RuntimeException(e);
    }
    return SnowFlakeIdService.getInstance().nextId();
  }

  public Long nextId(){return SnowFlakeIdService.getInstance().nextId();}
}
