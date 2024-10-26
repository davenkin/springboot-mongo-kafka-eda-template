package davenkin.springboot.web.department;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DepartmentApplicationService {
    private final DepartmentFactory departmentFactory;
    private final DepartmentRepository departmentRepository;


    @Transactional
    public String createDepartment(String name) {
        Department department = this.departmentFactory.createDepartment(name);
        this.departmentRepository.save(department);
        log.info("Created department {}.", department.getId());
        return department.getId();
    }

    @Transactional
    public void addUserToDepartment(String userId, String departmentId) {
        Department department = this.departmentRepository.byId(departmentId);
        department.addUser(userId);
        this.departmentRepository.save(department);
        log.info("Added user {} to department {}.", userId, departmentId);
    }
}
