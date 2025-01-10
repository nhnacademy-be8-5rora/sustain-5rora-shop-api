package store.aurora.book.util;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "aladinBookClient", url = "${aladin.api.base-url}")
public interface AladinBookClient {

    @GetMapping("/ItemSearch.aspx")
    String searchBooks(
            @RequestParam String ttbKey,
            @RequestParam String query,
            @RequestParam String queryType,
            @RequestParam String searchTarget,
            @RequestParam int start,
            @RequestParam int maxResults,
            @RequestParam String output,
            @RequestParam String version
    );

    @GetMapping("/ItemLookUp.aspx")
    String getBookDetails(
            @RequestParam String ttbKey,
            @RequestParam String itemIdType,
            @RequestParam String itemId,
            @RequestParam String output,
            @RequestParam String version
    );
}