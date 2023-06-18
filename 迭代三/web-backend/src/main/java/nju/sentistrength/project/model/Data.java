package nju.sentistrength.project.model;

import java.util.Date;
import javax.persistence.*;

public class Data {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "issue_number")
    private Integer issueNumber;

    @Column(name = "internal_issue_number")
    private Integer internalIssueNumber;

    private String username;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "ended_at")
    private Date endedAt;

    @Column(name = "is_pull_request")
    private Boolean isPullRequest;

    private String labels;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "version_number")
    private String versionNumber;

    @Column(name = "positive_score")
    private Integer positiveScore;

    @Column(name = "negative_score")
    private Integer negativeScore;

    private String content;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return issue_number
     */
    public Integer getIssueNumber() {
        return issueNumber;
    }

    /**
     * @param issueNumber
     */
    public void setIssueNumber(Integer issueNumber) {
        this.issueNumber = issueNumber;
    }

    /**
     * @return internal_issue_number
     */
    public Integer getInternalIssueNumber() {
        return internalIssueNumber;
    }

    /**
     * @param internalIssueNumber
     */
    public void setInternalIssueNumber(Integer internalIssueNumber) {
        this.internalIssueNumber = internalIssueNumber;
    }

    /**
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return created_at
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return ended_at
     */
    public Date getEndedAt() {
        return endedAt;
    }

    /**
     * @param endedAt
     */
    public void setEndedAt(Date endedAt) {
        this.endedAt = endedAt;
    }

    /**
     * @return is_pull_request
     */
    public Boolean getIsPullRequest() {
        return isPullRequest;
    }

    /**
     * @param isPullRequest
     */
    public void setIsPullRequest(Boolean isPullRequest) {
        this.isPullRequest = isPullRequest;
    }

    /**
     * @return labels
     */
    public String getLabels() {
        return labels;
    }

    /**
     * @param labels
     */
    public void setLabels(String labels) {
        this.labels = labels;
    }

    /**
     * @return project_name
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * @param projectName
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * @return version_number
     */
    public String getVersionNumber() {
        return versionNumber;
    }

    /**
     * @param versionNumber
     */
    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    /**
     * @return positive_score
     */
    public Integer getPositiveScore() {
        return positiveScore;
    }

    /**
     * @param positiveScore
     */
    public void setPositiveScore(Integer positiveScore) {
        this.positiveScore = positiveScore;
    }

    /**
     * @return negative_score
     */
    public Integer getNegativeScore() {
        return negativeScore;
    }

    /**
     * @param negativeScore
     */
    public void setNegativeScore(Integer negativeScore) {
        this.negativeScore = negativeScore;
    }

    /**
     * @return content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }
}