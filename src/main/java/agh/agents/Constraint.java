package agh.agents;

import java.util.Map;

public class Constraint {

    private Map<String, String> _map;

    public Constraint(Map<String, String> map) {
        _map = map;
    }

    public boolean IsGood(Map<String, String> agentView) {
        for (Map.Entry<String, String> entry : _map.entrySet()) {
            if (!agentView.containsKey(entry.getKey()) || !agentView.get(entry.getKey()).equals(entry.getValue())) {
                return true;
            }
        }

        return false;
    }
}
