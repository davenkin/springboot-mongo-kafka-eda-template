package davenkin.springboot.web.department;

import davenkin.springboot.web.common.infrastructure.MongoBaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public class DepartmentRepository extends MongoBaseRepository<Department> {
}
