package davenkin.springboot.web.common;

import static java.util.Objects.requireNonNull;

import static davenkin.springboot.web.common.CommonUtils.requireNonBlank;
import static davenkin.springboot.web.common.CommonUtils.singleParameterizedArgumentClassOf;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class MongoBaseRepository<AR extends AggregateRoot> {
  private final Map<String, Class> repositoryArClassMap = new ConcurrentHashMap<>();

  @Autowired
  protected MongoTemplate mongoTemplate;

  @Autowired
  private PublishingDomainEventDao domainEventDao;

  @Transactional
  public void save(AR it) {
    requireNonNull(it, arName() + " must not be null.");
    requireNonBlank(it.getId(), arName() + " ID must not be blank.");

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
    String className = this.getClass().getName();

    if (!repositoryArClassMap.containsKey(className)) {
      Class<?> arClass = singleParameterizedArgumentClassOf(this.getClass());
      if (arClass != null) {
        repositoryArClassMap.put(className, arClass);
      }
    }

    return repositoryArClassMap.get(className);
  }

  private String arName() {
    return arClass().getSimpleName();
  }

  private void saveEvents(List<DomainEvent> events) {
    if (!isEmpty(events)) {
      domainEventDao.stage(events);
    }
  }
}
