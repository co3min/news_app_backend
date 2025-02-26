package ebusiness.csie.ro.IAD_NEWS.service;

import ebusiness.csie.ro.IAD_NEWS.model.News;
import ebusiness.csie.ro.IAD_NEWS.repository.NewsRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {

    private final NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public List<News> searchByTitle(String keyword) {
        return newsRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public List<News> searchBySource(String source) {
        return newsRepository.findBySourceContainingIgnoreCase(source);
    }

    public  List<News> searchNews(String keyword, String source){
        if(keyword != null && !keyword.isEmpty() && source != null && !source.isEmpty()){
            return newsRepository.findByTitleContainingIgnoreCaseAndSourceContainingIgnoreCase(keyword, source);
        }else if (keyword != null && !keyword.isEmpty()) {
            return searchByTitle(keyword);
        } else if (source != null && !source.isEmpty()) {
            return searchBySource(source);
        } else {
            return getAllNews();
        }
    }

    public List<News> getAllNews() {
        return newsRepository.findAll();
    }

    public List<News> getSortedNews(String sortBy, String sortOrder, String keyword, String source) {

        List<News> filteredNews = searchNews(keyword, source);

        Sort.Direction direction = Sort.Direction.fromString(sortOrder);

        if ("publishedDate".equals(sortBy)) {

            filteredNews.sort((news1, news2) -> {
                if (direction == Sort.Direction.ASC) {
                    return news1.getPublishedDate().compareTo(news2.getPublishedDate());
                } else {
                    return news2.getPublishedDate().compareTo(news1.getPublishedDate());
                }
            });

        }

        return filteredNews;
    }

}
