package practice.paymentserver.global.redis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import practice.paymentserver.global.redis.entity.RedisRefreshToken;

@Repository
public interface RedisRepository extends CrudRepository<RedisRefreshToken, Long> {
}
