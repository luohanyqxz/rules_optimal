package rules;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Result implements Serializable {
    @Serial
    private static final long serialVersionUID = -3872928865743346529L;
    Set<String> idSet = new HashSet<>();
    Set<Rule> ruleSet = new HashSet<>();
    int callCnt;
    int blkCnt;

    public void updateData() {
        for (Rule rule : ruleSet) {
            this.getIdSet().add(rule.getRuleId());
        }
    }

    public Set<String> getIdSet() {
        return idSet;
    }

    public void setIdSet(Set<String> idSet) {
        this.idSet = idSet;
    }

    public Set<Rule> getRuleSet() {
        return ruleSet;
    }

    public void setRuleSet(Set<Rule> ruleSet) {
        this.ruleSet = ruleSet;
    }

    public int getCallCnt() {
        return callCnt;
    }

    public void setCallCnt(int callCnt) {
        this.callCnt = callCnt;
    }

    public int getBlkCnt() {
        return blkCnt;
    }

    public void setBlkCnt(int blkCnt) {
        this.blkCnt = blkCnt;
    }

    @Override
    public String toString() {
        return "Result{" +
                "idSet=" + idSet +
                ", ruleSet=" + ruleSet +
                ", callCnt=" + callCnt +
                ", blkCnt=" + blkCnt +
                '}';
    }
}
