package com.example.findanime;

import com.example.findanime.DTO.AnimeInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "anime",
            url = "${api.url}")
public interface AnimeProxy {
    @GetMapping("/search")
    AnimeInfo getAnimeByUrl(@RequestParam String url);

//    @PostMapping(value = "search")
//    AnimeInfo getAnimeByPhoto(@RequestBody);

}
