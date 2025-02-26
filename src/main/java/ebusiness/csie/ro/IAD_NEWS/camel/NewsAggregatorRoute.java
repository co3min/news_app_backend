package ebusiness.csie.ro.IAD_NEWS.camel;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import ebusiness.csie.ro.IAD_NEWS.model.News;
import ebusiness.csie.ro.IAD_NEWS.repository.NewsRepository;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Component
public class NewsAggregatorRoute extends RouteBuilder{

    private final NewsRepository newsRepository;
    private int countDigi24 = 0;
    private int countHotnews = 0;

    public NewsAggregatorRoute(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }


    @Override
    public void configure() throws Exception {
        from("rss:https://digi24.ro/rss?splitEntries=true")
                .split(simple("${body.entries}"))
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {

                        if (countDigi24 >= 15) {
                            return;
                        }

                        Object body = exchange.getIn().getBody();
                        if (body instanceof SyndEntry) {
                            SyndEntry entry = (SyndEntry) body;
                            saveNews(entry, "Digi24");
                            countDigi24++;
                        }
                    }
                });

        from("rss:https://hotnews.ro/feed?splitEntries=true")
                .split(simple("${body.entries}"))
                .filter(exchange -> {
                    SyndEntry entry = exchange.getIn().getBody(SyndEntry.class);
                    return entry.getCategories().stream()
                            .anyMatch(category -> category.getName().equalsIgnoreCase("Actualitate")); // Doar știrile din categoria "Actualitate"
                })
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {

                        if (countHotnews >= 15) {
                            return;
                        }

                        Object body = exchange.getIn().getBody();
                        if (body instanceof SyndEntry) {
                            SyndEntry entry = (SyndEntry) body;
                            saveNews(entry, "Hotnews");
                            countHotnews++;

                        } else {
                            log.warn("Elementul curent nu este o intrare validă: {}", body);
                        }
                    }
                });
    }

    private void saveNews(SyndEntry entry, String source){
        String title = entry.getTitle();
        String link = entry.getLink();
        String description = (entry.getDescription() != null) ? entry.getDescription().getValue() : "Fără descriere";
        Date pubDate = entry.getPublishedDate();

        log.info("Titlu: {}", title);
        log.info("Link: {}", link);
        log.info("Descriere: {}", description);
        log.info("Data publicării: {}", pubDate != null ? pubDate.toString() : "Necunoscută");

        if(!newsRepository.existsByLink(link)){
            News news = new News();
            news.setTitle(title);
            news.setLink(link);
            news.setDescription(description);
            if(pubDate != null){
                news.setPublishedDate(pubDate);
            }
            news.setSource(source);
            newsRepository.save(news);
        }
    }
}
