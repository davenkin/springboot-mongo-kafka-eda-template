package davenkin.springboot.web.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static davenkin.springboot.web.common.CommonUtils.requireNonBlank;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class MongoBaseRepository<AR extends AggregateRoot> {
    private final Map<String, Class> classMapper = new HashMap<>();

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    private PublishingDomainEventDao domainEventDao;

    @Transactional
    public void save(AR it) {
        requireNonNull(it, arName() + " must not be null.");

        if (!isEmpty(it.getEvents())) {
            saveEvents(it.getEvents());
        }

        mongoTemplate.save(it);
    }

    @Transactional
    public void delete(AR it) {
        requireNonNull(it, arName() + " must not be null.");

        if (!isEmpty(it.getEvents())) {
            saveEvents(it.getEvents());
        }
        mongoTemplate.remove(it);
    }


    public AR byId(String id) {
        requireNonBlank(id, arName() + " ID must not be blank.");
        return (AR) mongoTemplate.findById(id, arClass());
    }

    private Class arClass() {
        String className = getClass().getSimpleName();

        if (!classMapper.containsKey(className)) {
            Type genericSuperclass = getClass().getGenericSuperclass();
            Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
            classMapper.put(className, (Class) actualTypeArguments[0]);
        }

        return classMapper.get(className);
    }

    private String arName() {
        return arClass().getSimpleName();
    }

    private void saveEvents(List<DomainEvent> events) {
        if (!isEmpty(events)) {
            domainEventDao.insert(events);
        }
    }
}
