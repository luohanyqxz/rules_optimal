package rules;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Rule implements Comparable<Rule>, Serializable {
    @Serial
    private static final long serialVersionUID = 2212155885411812847L;
    private String ruleId;
    private String ruleName;
    private String ruleDesc;
    private int ruleUserCnt;
    private double ruleLift;
    private List<String> uidList;
    private List<String> uidBlkList;

    public Rule(String ruleId, String ruleName, String ruleDesc, int ruleUserCnt, double ruleLift
            , List<String> uidList, List<String> uidBlkList) {
        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.ruleDesc = ruleDesc;
        this.ruleUserCnt = ruleUserCnt;
        this.ruleLift = ruleLift;
        this.uidList = uidList;
        this.uidBlkList = uidBlkList;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleDesc() {
        return ruleDesc;
    }

    public void setRuleDesc(String ruleDesc) {
        this.ruleDesc = ruleDesc;
    }

    public int getRuleUserCnt() {
        return ruleUserCnt;
    }

    public void setRuleUserCnt(int ruleUserCnt) {
        this.ruleUserCnt = ruleUserCnt;
    }

    public double getRuleLift() {
        return ruleLift;
    }

    public void setRuleLift(double ruleLift) {
        this.ruleLift = ruleLift;
    }

    public List<String> getUidList() {
        return uidList;
    }

    public void setUidList(List<String> uidList) {
        this.uidList = uidList;
    }

    public List<String> getUidBlkList() {
        return uidBlkList;
    }

    public void setUidBlkList(List<String> uidBlkList) {
        this.uidBlkList = uidBlkList;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "ruleId='" + ruleId + '\'' +
                ", ruleName='" + ruleName + '\'' +
                ", ruleDesc='" + ruleDesc + '\'' +
                ", ruleEffect=" + ruleUserCnt +
                ", ruleValue=" + ruleLift +
                ", uidList=" + uidList.toString() +
                ", uidBlkList=" + uidBlkList.toString() +
                '}';
    }

    @Override
    public int compareTo(Rule o) {
        if (this.ruleLift < o.ruleLift) {
            return 1;
        } else if (this.ruleLift == o.ruleLift) {
            return 1;
        }
        return -1;
    }

    @Override
    public boolean equals(Object obj) {
        return this.ruleId == ((Rule) obj).getRuleId();
    }
}
