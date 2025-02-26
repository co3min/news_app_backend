package ebusiness.csie.ro.IAD_NEWS.repository;

import ebusiness.csie.ro.IAD_NEWS.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    List<News> findByTitleContainingIgnoreCase(String title);

    List<News> findBySourceContainingIgnoreCase(String source);

    List<News> findByTitleContainingIgnoreCaseAndSourceContainingIgnoreCase(String keyword, String source);

    boolean existsByLink(String link);
}
