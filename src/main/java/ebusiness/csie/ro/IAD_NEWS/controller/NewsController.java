package ebusiness.csie.ro.IAD_NEWS.controller;

import ebusiness.csie.ro.IAD_NEWS.model.News;
import ebusiness.csie.ro.IAD_NEWS.service.NewsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5001")
@RequestMapping("/api")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/news")
    public List<News> getAllNews() {
        return newsService.getAllNews();
    }

    @GetMapping("/sortedNews")
    public List<News> getSortedNews(@RequestParam(required = false) String sortBy,
                                    @RequestParam(required = false, defaultValue = "desc") String sortOrder,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) String source) {
        return newsService.getSortedNews(sortBy, sortOrder, keyword, source);
    }

    @GetMapping("/search")
    public List<News> searchNewsByTitle(@RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) String source) {
        return newsService.searchNews(keyword, source);
    }

}
