package store.aurora.book.util;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "aladinBookClient", url = "${aladin.api.base-url}")
public interface AladinBookClient {

    @GetMapping("/ItemSearch.aspx")
    String searchBooks(
            @RequestParam("ttbkey") String ttbKey,
            @RequestParam("Query") String query,
            @RequestParam("QueryType") String queryType,
            @RequestParam("MaxResults") int maxResults,
            @RequestParam("Start") int start,
            @RequestParam("SearchTarget") String searchTarget,
            @RequestParam("Output") String output,
            @RequestParam("Version") String version // 추가된 부분

    );


    @GetMapping("/ItemLookUp.aspx")
    String getBookDetails(
            @RequestParam("ttbkey") String ttbKey,
            @RequestParam("itemIdType") String itemIdType,
            @RequestParam("ItemId") String itemId,
            @RequestParam("Output") String output,
            @RequestParam("Version") String version
    );
}