package com.zju.qipingzhiyan;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Stats;
import org.elasticsearch.tasks.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AggTest {
    private final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

    private final RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(
                    HttpHost.create("http://124.71.196.104:9200")
            ).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                    credentialsProvider.setCredentials(AuthScope.ANY,
                            new UsernamePasswordCredentials("elastic", "elastic"));
                    httpAsyncClientBuilder.disableAuthCaching();
                    return httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }
            }));





    @Test
    public void test1() throws IOException {
        SearchRequest request = new SearchRequest("review");
        request.source().query(QueryBuilders.matchPhraseQuery("reviewContent","喜欢"));
        request.source().size(0).aggregation(AggregationBuilders.terms("companyAgg")
                .field("companyId")).size(10);
        handleReviewResult(request);
    }

    @Test
    public void test2() throws IOException {
        SearchRequest request = new SearchRequest("review");
        request.source().size(0).aggregation(AggregationBuilders.terms("ratingAgg")
                        .field("companyId").size(10)
                .subAggregation(AggregationBuilders.stats("ratings").field("rating")));
        handleAggResult(request);
    }

    //嵌套聚合stats
    private void handleAggResult(SearchRequest request) throws IOException {
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        Aggregations aggregations = response.getAggregations();
        Terms terms = aggregations.get("ratingAgg");
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            Stats stats = bucket.getAggregations().get("ratings");
            System.out.println(stats.getCount());
            System.out.println(stats.getMin());
            System.out.println(stats.getMax());
            System.out.println(stats.getAvg());
            System.out.println(stats.getSum());
        }
    }

    //解析ES查询评论结果
    private void handleReviewResult(SearchRequest request) throws IOException {
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        Aggregations aggregations = response.getAggregations();
        Terms terms = aggregations.get("companyAgg");
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            String key = bucket.getKeyAsString();
            System.out.println(key);
        }


    }
}
