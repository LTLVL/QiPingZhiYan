package com.zju.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zju.common.Response;
import com.zju.mapper.CompanyMapper;
import com.zju.mapper.ReviewMapper;
import com.zju.pojo.Company;
import com.zju.pojo.CompanyAndReview;
import com.zju.pojo.Review;
import com.zju.service.CompanyService;
import com.zju.service.ReviewService;
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
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {
    @Autowired
    private ReviewService reviewService;

    private final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

    private RestHighLevelClient client = new RestHighLevelClient(
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

    @Override
    public Response<List<CompanyAndReview>> selectAllSub() {
        LambdaQueryWrapper<Company> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Company::getSubjectiveRating);
        List<Company> companies = this.list(queryWrapper);
        ArrayList<CompanyAndReview> result = new ArrayList<>();
        companies.forEach(company -> {
            CompanyAndReview companyAndReview = new CompanyAndReview();
            companyAndReview.setCompany(company);
            LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Review::getCompanyId, company.getId());
            List<Review> reviews = reviewService.list(wrapper);
            companyAndReview.setReviews((ArrayList<Review>) reviews);
            result.add(companyAndReview);
        });
        return Response.success(result);
    }

    @Override
    public Response<List<CompanyAndReview>> selectAllOb() {
        LambdaQueryWrapper<Company> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Company::getObjectiveRating);
        List<Company> companies = this.list(queryWrapper);
        ArrayList<CompanyAndReview> result = new ArrayList<>();
        companies.forEach(company -> {
            CompanyAndReview companyAndReview = new CompanyAndReview();
            companyAndReview.setCompany(company);
            LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Review::getCompanyId, company.getId());
            List<Review> reviews = reviewService.list(wrapper);
            companyAndReview.setReviews((ArrayList<Review>) reviews);
            result.add(companyAndReview);
        });
        return Response.success(result);
    }

    @Override
    public Response<List<CompanyAndReview>> selectByName(String companyName) throws IOException {
        SearchRequest request = new SearchRequest("company");
        request.source()
                .query(QueryBuilders.matchQuery("all",companyName));
        List<Company> companies = handleCompanyResult(request);
        if (companies.size() == 0) {
            //todo:调用爬虫查询公司数据
            return Response.error("没有此公司");
        }
        List<CompanyAndReview> companyAndReviews = new ArrayList<>();
        for (Company company : companies) {
            CompanyAndReview companyAndReview = new CompanyAndReview();
            companyAndReview.setCompany(company);
            LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Review::getCompanyId, company.getId());
            List<Review> reviews = reviewService.list(wrapper);
            companyAndReview.setReviews((ArrayList<Review>) reviews);
            companyAndReviews.add(companyAndReview);
        }
        return Response.success(companyAndReviews);
    }

    @Override
    public Response<List<CompanyAndReview>> selectByReview(String review) throws IOException {
        SearchRequest request = new SearchRequest("review");
        request.source()
                .query(QueryBuilders.matchQuery("reviewContent",review));
        List<Review> reviews = handleReviewResult(request);
        Set<Integer> companyIds = new HashSet<>();
        for (Review review1 : reviews) {
            companyIds.add(review1.getCompanyId());
        }
        List<CompanyAndReview> companyAndReviews = new ArrayList<>();
        for (Integer companyId : companyIds) {
            CompanyAndReview companyAndReview = new CompanyAndReview();
            Company company = this.getById(companyId);
            companyAndReview.setCompany(company);
            LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Review::getCompanyId, company.getId());
            List<Review> companyReviews = reviewService.list(wrapper);
            companyAndReview.setReviews((ArrayList<Review>) companyReviews);
            companyAndReviews.add(companyAndReview);
        }
        return Response.success(companyAndReviews);
    }

    //解析ES查询评论结果
    private List<Review> handleReviewResult(SearchRequest request) throws IOException {
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        long total = hits.getTotalHits().value;
        SearchHit[] searchHits = hits.getHits();
        ArrayList<Review> reviews = new ArrayList<>();
        for (SearchHit searchHit : searchHits) {
            String json = searchHit.getSourceAsString();
            Review review = JSON.parseObject(json, Review.class);
            reviews.add(review);
        }
        return reviews;
    }

    //解析ES查询企业结果
    private List<Company> handleCompanyResult(SearchRequest request) throws IOException {
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        long total = hits.getTotalHits().value;
        SearchHit[] searchHits = hits.getHits();
        ArrayList<Company> companies = new ArrayList<>();
        for (SearchHit searchHit : searchHits) {
            String json = searchHit.getSourceAsString();
            Company company = JSON.parseObject(json, Company.class);
            companies.add(company);
        }
        return companies;
    }
}
