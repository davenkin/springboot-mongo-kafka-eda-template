package davenkin.springboot.web.department;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DepartmentFactory {

    public Department createDepartment(String name) {
        return new Department(name);
    }
}
