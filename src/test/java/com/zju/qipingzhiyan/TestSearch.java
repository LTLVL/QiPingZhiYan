package com.zju.qipingzhiyan;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.zju.pojo.Review;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestSearch {
    private RestHighLevelClient client;

    @BeforeEach
    public void before() {
        client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://120.27.238.231:19200")
        ));
    }

    @AfterEach
    public void after() throws IOException {
        this.client.close();
    }

    //查询所有文档
    @Test
    public void testMatchAll() throws IOException {
        SearchRequest request = new SearchRequest("review");
        request.source().query(QueryBuilders.matchAllQuery()).sort("rating", SortOrder.DESC);
        handleResult(request);
    }

    //指定查询：评论内容中带有“不错”的评论
    @Test
    public void testMatch() throws IOException {
        int page = 1, size = 5;
        SearchRequest request = new SearchRequest("review");
        request.source()
                .query(QueryBuilders.matchQuery("reviewContent","不错"))
                .sort("rating", SortOrder.DESC)
                .from((page - 1) * size).size(size);
        handleResult(request);
    }

    //布尔查询：
    @Test
    public void testBoolQuery() throws IOException {
        SearchRequest request = new SearchRequest("review");

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.termQuery("companyId","10001"))
                .filter(QueryBuilders.rangeQuery("rating").gt(4));

        request.source().query(boolQuery);
        handleResult(request);
    }

    //解析查询结果
    private void handleResult(SearchRequest request) throws IOException {
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        SearchHits hits = response.getHits();
        long total = hits.getTotalHits().value;
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            String json = searchHit.getSourceAsString();
            Review review = JSON.parseObject(json, Review.class);
            System.out.println(review);
        }
    }


}
