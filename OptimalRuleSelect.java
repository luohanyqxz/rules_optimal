package rules;

import javax.swing.plaf.synth.SynthLookAndFeel;
import java.util.*;

public class OptimalRuleSelect {
    public static final String split = "#";
    public ArrayList<Rule> rules = new ArrayList<>();
    public Map<String, Set<Rule>> dpMap = new TreeMap<>();
    public Map<String, Integer> dpLiftMap = new TreeMap<>();
    public Map<String, Integer> dpLiftCnt = new TreeMap<>();
    public Map<String, Integer> dpLiftScore = new TreeMap<>();


    public ArrayList<Rule> getRules() {
        return rules;
    }

    public void setRules(ArrayList<Rule> rules) {
        this.rules = rules;
    }

    public Map<String, Set<Rule>> getDpMap() {
        return dpMap;
    }

    public void setDpMap(Map<String, Set<Rule>> dpMap) {
        this.dpMap = dpMap;
    }

    public Map<String, Integer> getDpLiftMap() {
        return dpLiftMap;
    }

    public void setDpLiftMap(Map<String, Integer> dpLiftMap) {
        this.dpLiftMap = dpLiftMap;
    }

    public Map<String, Integer> getDpLiftCnt() {
        return dpLiftCnt;
    }

    public void setDpLiftCnt(Map<String, Integer> dpLiftCnt) {
        this.dpLiftCnt = dpLiftCnt;
    }

    public Map<String, Integer> getDpLiftScore() {
        return dpLiftScore;
    }

    public void setDpLiftScore(Map<String, Integer> dpLiftScore) {
        this.dpLiftScore = dpLiftScore;
    }

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

    public Set<Rule> safeRuleSetGet(Map<String, Set<Rule>> map, String key) {
        Set<Rule> ruleSet = new TreeSet<>();
        if (map.size() == 0 || map == null || !map.containsKey(key)) {
            return new HashSet<>();
        }
        ruleSet.addAll(map.get(key));
        return ruleSet;
    }

    public int safeRuleIntGet(Map<String, Integer> map, String key) {
        if (map.size() == 0 || map == null || !map.containsKey(key)) {
            return -1;
        }
        return map.get(key);
    }

    /**
     * @param callCondition 召回人数限制
     * @param blkCondition  黑名单用户限制
     * @return set<Rule>  最后的规则集合
     * @
     */
    public Result optimalAlgo(int callCondition, int blkCondition) {
        Collections.sort(rules);
//        System.out.println(rules);
        int ruleCnt = rules.size();
        int dp[][] = new int[ruleCnt + 1][callCondition + 1];
        //initial
        for (int i = 0; i <= rules.size(); i++) {
            dp[i][0] = 0;
            dpMap.put(key(i, 0), new HashSet<Rule>());
            dpLiftMap.put(key(i, 0), 0);
            dpLiftCnt.put(key(i, 0), 0);
            dpLiftScore.put(key(i, 0), 0);
        }
        for (int j = 0; j <= callCondition; j++) {
            dp[0][j] = 0;
            dpMap.put(key(0, j), new HashSet<Rule>());
            dpLiftMap.put(key(0, j), 0);
            dpLiftCnt.put(key(0, j), 0);
            dpLiftScore.put(key(0, j), 0);
        }
        //DP_Processing
        for (int i = 1; i <= rules.size(); i++) {
            for (int j = 1; j <= callCondition; j++) {
//                System.out.println("iterator:i=" + i + "     j=" + j);
                Rule pRule = rules.get(i - 1);
                String keyNow = key(i, j);
                String keyBefore = key(i - 1, j);
//                dpMap.put(keyNow, dpMap.get(keyBefore));
//                dpLiftMap.put(keyNow, dpLiftMap.get(keyBefore));
//                dpLiftCnt.put(keyNow, dpLiftCnt.get(keyBefore));
//                dpLiftScore.put(keyNow, dpLiftScore.get(keyBefore));
                if (rules.get(i - 1).getUidList().size() > j) {
                    dp[i][j] = dp[i - 1][j];
                    dpMap.put(keyNow, dpMap.get(keyBefore));
                    dpLiftMap.put(keyNow, dpLiftMap.get(keyBefore));
                    dpLiftCnt.put(keyNow, dpLiftCnt.get(keyBefore));
                    dpLiftScore.put(keyNow, dpLiftScore.get(keyBefore));
                } else {
                    int preValue = dp[i - 1][j];
//                    System.out.println("preValue="+preValue);
                    String keyOri=key(i-1,j);
                    int pCallUserCnt = pRule.getUidList().size();
                    String maxKey = keyNow;
                    for (int index = j - pCallUserCnt; index <= j; index++) {
                        if (index >= 0) {
//                            System.out.println("[]index="+index);
                            keyBefore = key(i - 1, index);
                            Set<Rule> beforeSet = safeRuleSetGet(dpMap, keyBefore);
                            beforeSet.add(pRule);
                            int newCallCnt = updateCallUserCnt(beforeSet);
                            if (newCallCnt > callCondition||newCallCnt>j) continue;
                            int newBlkCnt = updateBlkUserCnt(beforeSet);
                            int newScore = newBlkCnt;
//                            System.out.println("newBlkCnt="+newBlkCnt+" newScore="+newScore+" keyNow="+keyNow+" keyBefore="+keyBefore+" beforeSet="+beforeSet);
                            if (newScore >= preValue) {
                                maxKey = keyBefore;
                                dp[i][j]=newBlkCnt;
//                                dp[i][j] = dp[i - 1][index] + newBlkCnt;
                                dpMap.put(keyNow, beforeSet);
                                dpLiftMap.put(keyNow, newScore);
                                dpLiftCnt.put(keyNow, newCallCnt);
                                dpLiftScore.put(keyNow, newScore);
                            } else {
                                maxKey = keyOri;
                                dp[i][j] = dp[i - 1][j];
                                dpMap.put(keyNow, dpMap.get(keyOri));
                                dpLiftMap.put(keyNow, dpLiftMap.get(keyOri));
                                dpLiftCnt.put(keyNow, dpLiftCnt.get(keyOri));
                                dpLiftScore.put(keyNow, dpLiftScore.get(keyOri));
                            }
                        }
//                        System.out.println("[]dpMap="+dpMap);
                    }
//                    String tmpKey = maxKey;
//                    if (safeRuleIntGet(dpLiftScore, maxKey) < preValue) {
//                        tmpKey = key(i - 1, j);
//                    }
//                    System.out.println("[]tmpKey="+tmpKey+" maxKey="+maxKey);
//                    if (safeRuleIntGet(dpLiftScore, maxKey) > preValue) {
//                        dp[i][j] = dpLiftScore.get(maxKey);
//                        dpMap.put(keyNow, dpMap.get(maxKey));
//                        dpLiftMap.put(keyNow, dpLiftMap.get(maxKey));
//                        dpLiftCnt.put(keyNow, dpLiftCnt.get(maxKey));
//                        dpLiftScore.put(keyNow, dpLiftScore.get(maxKey));
//                    } else {
//                        dp[i][j] = dpLiftScore.get(tmpKey);
//                        dpMap.put(keyNow, dpMap.get(tmpKey));
//                        dpLiftMap.put(keyNow, dpLiftMap.get(tmpKey));
//                        dpLiftCnt.put(keyNow, dpLiftCnt.get(tmpKey));
//                        dpLiftScore.put(keyNow, dpLiftScore.get(tmpKey));
//                    }
                }
//                System.out.println("dpMap:" + dpMap);
//                System.out.println("dpLiftMap:" + dpLiftMap);
//                System.out.println("dpLiftCnt:" + dpLiftMap);
//                System.out.println("dpLiftScore:" + dpLiftMap);
//                System.out.println("-----------------------");
            }
        }
        Result result = new Result();
        result.setRuleSet(dpMap.get(key(rules.size(), callCondition)));
        result.setCallCnt(safeRuleIntGet(dpLiftCnt, key(rules.size(), callCondition)));
        result.setBlkCnt(safeRuleIntGet(dpLiftScore, key(rules.size(), callCondition)));
//        for (int i = 0; i < ruleCnt + 1; i++) {
//            System.out.println();
//            for (int j = 0; j < callCondition + 1; j++) {
//                System.out.print(dp[i][j] + " ");
//            }
//        }
//        System.out.println();
//        System.out.println(result);
        result.updateData();
        return result;
    }

    public static void main(String[] args) {
        List<String> call1 = new ArrayList<>();
        List<String> blk1 = new ArrayList<>();
        call1.add("1");
        call1.add("2");
        call1.add("3");
        blk1.add("1");
        blk1.add("2");
        blk1.add("3");
        List<String> call2 = new ArrayList<>();
        List<String> blk2 = new ArrayList<>();
        call2.add("2");
        call2.add("3");
        call2.add("4");
        call2.add("5");
        blk2.add("2");
        blk2.add("3");
        blk2.add("4");
        blk2.add("5");
        Rule rule1 = new Rule("rule01", "", "", 3, 3, call1, blk1);
        Rule rule2 = new Rule("rule02", "", "", 4, 4, call2, blk2);
        OptimalRuleSelect select = new OptimalRuleSelect();
        ArrayList<Rule> ruleList = new ArrayList<>();
        ruleList.add(rule1);
        ruleList.add(rule2);
        select.setRules(ruleList);
//        System.out.println(ruleList);
        System.out.println(select.optimalAlgo(6, 3));
    }
}
