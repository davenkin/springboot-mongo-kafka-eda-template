package davenkin.springboot.web.user.infrastructure;

import davenkin.springboot.web.common.MongoBaseRepository;
import davenkin.springboot.web.user.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository extends MongoBaseRepository<User> {
}
