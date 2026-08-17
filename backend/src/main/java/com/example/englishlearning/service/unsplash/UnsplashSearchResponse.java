package com.example.englishlearning.service.unsplash;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UnsplashSearchResponse {

    private List<Result> results;

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        private String id;
        private Urls urls;
        private User user;
        private Links links;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Urls getUrls() {
            return urls;
        }

        public void setUrls(Urls urls) {
            this.urls = urls;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Links getLinks() {
            return links;
        }

        public void setLinks(Links links) {
            this.links = links;
        }

        public String getUrl() {
            return links != null ? links.getHtml() : null;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Urls {
        private String regular;

        public String getRegular() {
            return regular;
        }

        public void setRegular(String regular) {
            this.regular = regular;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {
        private String name;
        private String portfolio_url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPortfolioUrl() {
            return portfolio_url;
        }

        public void setPortfolioUrl(String portfolio_url) {
            this.portfolio_url = portfolio_url;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Links {
        private String html;

        public String getHtml() {
            return html;
        }

        public void setHtml(String html) {
            this.html = html;
        }
    }
}
