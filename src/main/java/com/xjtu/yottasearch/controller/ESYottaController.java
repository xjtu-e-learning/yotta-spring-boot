package com.xjtu.yottasearch.controller;


import com.xjtu.yottasearch.index.ESYotta;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/es")
public class ESYottaController {

    @PostMapping("/indexOneTopic")
    public List<String> indexOneTopic(long topicID) throws SQLException {
        return ESYotta.indexOneTopic(topicID);
    }

    @PostMapping("/indexOneDomain")
    public Set<Long> indexOneDomain(long domainID) throws SQLException {
        return ESYotta.indexOneDomain(domainID);
    }


    @PostMapping("/indexOneSubject")
    public Set<String> indexOneSubject(long subjectID) throws SQLException {
        return ESYotta.indexOneSubject(subjectID);
    }

    @PutMapping("/creatIndexAndMapping")
    public String creatIndexAndMapping() throws IOException {
        return ESYotta.creatIndexAndMapping();
    }


    @GetMapping("/search")
    public Map search(String q,
                      @RequestParam(required = false, defaultValue = "") String subjectName,
                      @RequestParam(required = false, defaultValue = "") String domainName,
                      @RequestParam(required = false, defaultValue = "") String topicName,
                      @RequestParam(required = false, defaultValue = "") String facetName,
                      @RequestParam(required = false, defaultValue = "") String facetLayer,
                      @RequestParam(required = false, defaultValue = "") String assembleSource,
                      @RequestParam(required = false, defaultValue = "") String assembleType,
                      @RequestParam(required = false, defaultValue = "0") int page,
                      @RequestParam(required = false, defaultValue = "10") int size) throws IOException, SQLException {
        return ESYotta.search(q, subjectName, domainName, topicName, facetName, facetLayer, assembleSource, assembleType, page, size);
    }

}
