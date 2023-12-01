package com.zju.qipingzhiyan;

import com.alibaba.fastjson2.JSON;
import com.zju.pojo.Company;
import com.zju.pojo.Review;
import com.zju.service.CompanyService;
import com.zju.service.ReviewService;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest
public class TestRestClient {
    private RestHighLevelClient client;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private CompanyService companyService;

    @Test
    public void text() {
        System.out.println(this.client);
    }
    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();


    @BeforeEach
    public void before() {
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("elastic", "elastic"));
        client = new RestHighLevelClient(
                RestClient.builder(
                HttpHost.create("http://124.71.196.104:9200")
        ).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                        httpAsyncClientBuilder.disableAuthCaching();
                        return httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                }));
    }

    @AfterEach
    public void after() throws IOException {
        this.client.close();
    }

    //创建索引
    @Test
    public void testCreateIndex() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("review");
        createIndexRequest.source(create, XContentType.JSON);
        client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
    }

    //删除索引
    @Test
    public void testDeleteIndex() throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("review");
        client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
    }

    //查询索引是否存在
    @Test
    public void testGetIndex() throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest("review");
        boolean exists = client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    //插入文档
    @Test
    public void testIndexDocument() throws IOException {
        Review review = reviewService.getById(1);

        IndexRequest request = new IndexRequest("review").id(review.getId().toString());

        request.source(JSON.toJSONString(review), XContentType.JSON);

        IndexResponse index = client.index(request, RequestOptions.DEFAULT);
    }

    //插入文档
    @Test
    public void testIndex() throws IOException {
        List<Company> list = companyService.list();
        BulkRequest request = new BulkRequest();
        for (Company company : list) {
            request.add(new IndexRequest("company")
                    .id(company.getId().toString())
                    .source(JSON.toJSONString(company),XContentType.JSON));
        }
        client.bulk(request, RequestOptions.DEFAULT);
    }

    //批量插入
    @Test
    public void testBulk() throws IOException {
        List<Review> list = reviewService.list();
        BulkRequest request = new BulkRequest();
        for (Review review : list) {
            request.add(new IndexRequest("review")
                    .id(review.getId().toString())
                    .source(JSON.toJSONString(review),XContentType.JSON));
        }
        client.bulk(request, RequestOptions.DEFAULT);
    }

    //查询文档
    @Test
    public void testGetDocument() throws IOException {

        GetRequest request = new GetRequest("review", "1");

        GetResponse response = client.get(request, RequestOptions.DEFAULT);

        String json = response.getSourceAsString();

        System.out.println(json);
        //{"companyId":10001,"id":1,"rating":4.7,
        // "reviewContent":"软件工程管理","reviewSource":"www.123.com",
        // "reviewTime":"2023-01-11","reviewerName":"liu"}
    }

    //增量更新
    @Test
    public void testUpdateDocument() throws IOException {
        Review review = reviewService.getById(1);
        UpdateRequest request = new UpdateRequest("review", review.getId().toString());
        request.doc(
                "rating", "2.5"
        );
        client.update(request, RequestOptions.DEFAULT);
    }

    //删除文档
    @Test
    public void testDeleteDocument() throws IOException{
        DeleteRequest request = new DeleteRequest("review", "1");
        client.delete(request, RequestOptions.DEFAULT);
    }

    private final String create = "{\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"companyId\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"reviewerName\": {\n" +
            "        \"type\": \"keyword\", \n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"reviewTime\": {\n" +
            "        \"type\": \"date\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"reviewContent\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_max_word\"\n" +
            "      },\n" +
            "      \"rating\": {\n" +
            "        \"type\": \"float\"\n" +
            "      },\n" +
            "      \"reviewSource\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

}
