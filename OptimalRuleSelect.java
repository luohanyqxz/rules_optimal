package rules;

import java.util.*;

public class OptimalRuleSelect {
    public static final String split = "#";
    public ArrayList<Rule> rules = new ArrayList<>();
    public Map<String, Set<Rule>> dpMap = new HashMap<>();
    public Map<String, Integer> dpLiftMap = new HashMap<>();
    public Map<String, Integer> dpLiftCnt = new HashMap<>();
    public Map<String, Integer> dpLiftScore = new HashMap<>();

    public static String key(int i, int j) {
        return i + split + j;
    }

    public static double sumLift(List<Rule> ruleList) {
        double lift = 0.0;
        for (Rule rule : ruleList) {
            lift += rule.getRuleLift();
        }
        return lift;
    }

    public int updateCallUserCnt(Set<Rule> ruleSet) {
        Set<String> uids = new HashSet<>();
        for (Rule rule : ruleSet) {
            for (String uid : rule.getUidList()) {
                uids.add(uid);
            }
        }
        return uids.size();
    }

    public int updateBlkUserCnt(Set<Rule> ruleSet) {
        Set<String> uids = new HashSet<>();
        for (Rule rule : ruleSet) {
            for (String uid : rule.getUidBlkList()) {
                uids.add(uid);
            }
        }
        return uids.size();
    }

    public static int sumCallUserCnt(List<Rule> ruleList) {
        Set<String> sets = new HashSet<>();
        int cnt = 0;
        for (Rule rule : ruleList) {
            sets.addAll(rule.getUidList());
        }
        return sets.size();
    }

    public static int sumBlkUserCnt(List<Rule> ruleList) {
        Set<String> sets = new HashSet<>();
        int cnt = 0;
        for (Rule rule : ruleList) {
            sets.addAll(rule.getUidBlkList());
        }
        return sets.size();
    }

    /**
     * @param callCondition 召回人数限制
     * @param blkCondition  黑名单用户限制
     * @return set<Rule>  最后的规则集合
     * @
     */
    public Result optimalAlgo(int callCondition, int blkCondition) {
        Collections.sort(rules);
        int ruleCnt = rules.size();
        int dp[][] = new int[ruleCnt][callCondition];
        int userCnt = sumCallUserCnt(rules);
        //initial
        for (int i = 0; i < rules.size(); i++) {
            dp[i][0] = 0;
            dpMap.put(key(i,0), new HashSet<Rule>());
            dpLiftMap.put(key(i,0), 0);
            dpLiftCnt.put(key(i,0), 0);
            dpLiftScore.put(key(i,0), 0);
        }
        for (int j = 0; j <= callCondition; j++) {
            dp[0][j] = 0;
            dpMap.put(key(0,j), new HashSet<Rule>());
            dpLiftMap.put(key(0,j), 0);
            dpLiftCnt.put(key(0,j), 0);
            dpLiftScore.put(key(0,j), 0);
        }

        //DP_Processing
        for (int i = 1; i < rules.size(); i++) {
            for (int j = 1; j < callCondition; j++) {
                Rule pRule = rules.get(i);
                String keyNow = key(i, j);
                String keyBefore = key(i - 1, j);
                if (rules.get(i).getUidList().size() > j) {
                    dp[i][j] = dp[i - 1][j];
                    dpMap.put(keyNow, dpMap.get(keyBefore));
                    dpLiftMap.put(keyNow, dpLiftMap.get(keyBefore));
                    dpLiftCnt.put(keyNow, dpLiftCnt.get(keyBefore));
                    dpLiftScore.put(keyNow, dpLiftScore.get(keyBefore));
                } else {
                    int preValue = dp[i - 1][j];
                    int pCallUserCnt = pRule.getUidList().size();
                    String maxKey = keyNow;
                    for (int index = j - pCallUserCnt; index <= j; index++) {
                        if (index >= 1) {
                            keyBefore = key(index, j);
                            Set<Rule> beforeSet = dpMap.get(keyBefore);
                            beforeSet.add(pRule);
                            dpMap.put(keyNow, beforeSet);
                            int newCallCnt = updateCallUserCnt(beforeSet);
                            int newBlkCnt = updateBlkUserCnt(beforeSet);
                            int newScore = newBlkCnt;
                            int oldScore = dpLiftMap.get(keyBefore);
                            if (newScore >= oldScore) {
                                maxKey = keyBefore;
                                dp[i][j] = dp[i - 1][index] + newBlkCnt;
                                dpMap.put(keyNow, beforeSet);
                                dpLiftMap.put(keyNow, newScore);
                                dpLiftCnt.put(keyNow, newCallCnt);
                                dpLiftScore.put(keyNow, newScore);
                            } else {
                                dpMap.put(keyNow, dpMap.get(keyBefore));
                                dpLiftMap.put(keyNow, dpLiftMap.get(keyBefore));
                                dpLiftCnt.put(keyNow, dpLiftCnt.get(keyBefore));
                                dpLiftScore.put(keyNow, dpLiftScore.get(keyBefore));
                            }
                        }
                    }
                    if (dpLiftScore.get(maxKey) >= preValue) {
                        dp[i][j] = dpLiftScore.get(maxKey);
                        dpMap.put(keyNow, dpMap.get(maxKey));
                        dpLiftMap.put(keyNow, dpLiftMap.get(maxKey));
                        dpLiftCnt.put(keyNow, dpLiftCnt.get(maxKey));
                        dpLiftScore.put(keyNow, dpLiftScore.get(maxKey));
                    }
                }
            }
        }
        Result result =new Result();
        result.setRuleSet(dpMap.get(key(rules.size(), callCondition)));
        result.setCallCnt(dpLiftCnt.get(key(rules.size(), callCondition)));
        result.setBlkCnt(dpLiftScore.get(key(rules.size(), callCondition)));
        result.updateData();
        return result;
    }

    public static void main(String[] args) {

    }
}
