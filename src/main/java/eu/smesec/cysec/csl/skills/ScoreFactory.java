/*-
 * #%L
 * CYSEC Standard Coach Language
 * %%
 * Copyright (C) 2020 - 2025 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package eu.smesec.cysec.csl.skills;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ScoreFactory {

  public enum ScoreType {
    VALUE, CAP
  }

  public record ScoreValue(ScoreType scoreType, double value) {
  }

  public static class Score {
    private final Map<String, List<ScoreValue>> scores = new HashMap<>();
    private boolean hidden = false;
    private final String id;

    public Score(String id) {
      this.id = id;
    }

    public String getId() {
      return id;
    }

    public void setHidden(boolean hidden) {
      this.hidden = hidden;
    }

    public boolean isHidden() {
      return hidden;
    }

    public void reset() {
      synchronized (scores) {
        scores.clear();
      }
    }

    public void revertQuestion(String id) {
      scores.put(id.toLowerCase(), new Vector<>());
    }

    private void addQuestionScore(String id, ScoreValue v) {
      if(id == null) {
        id = "__NULL__";
      }
      synchronized (scores) {
        if (scores.get(id.toLowerCase()) == null) {
          revertQuestion(id);
        }
        List<ScoreValue> sv = scores.get(id.toLowerCase());
        synchronized (sv) {
          sv.add(v);
        }
      }
    }

    public void add(String questionId, double value) {
      addQuestionScore(questionId,new ScoreValue(ScoreType.VALUE,value));
    }

    public void cap(String questionId, double value) {
      addQuestionScore(questionId,new ScoreValue(ScoreType.CAP,value));
    }

    public double getValue() {
      synchronized (scores) {
        double totalValue = 0;
        double minCap = Double.MAX_VALUE;
        for (List<ScoreValue> svl : scores.values()) {
          synchronized (svl) {
            for (ScoreValue sv : svl) {
              switch (sv.scoreType()) {
                case VALUE -> totalValue += sv.value();
                case CAP -> minCap = Math.min(minCap, sv.value());
                default -> throw new RuntimeException("Encountered unknown type of ScoreValue");
              }
            }
          }
        }
        return Math.min(minCap, totalValue);
      }
    }

  }

  private final Map<String, Score> scores = new HashMap<>();

  public Score getIntScore(String id) {
    synchronized (scores) {
      if (scores.get(id.toLowerCase()) == null) {
        scores.put(id.toLowerCase(), new Score(id));
      }
      return scores.get(id.toLowerCase());
    }
  }

  public Score[] getScoreList(boolean includeHidden) {
    List<Score> ret = new Vector<>();
    synchronized (scores) {
      for( Map.Entry<String,Score> s: scores.entrySet()) {
        if (!s.getValue().isHidden() || includeHidden) {
          ret.add(s.getValue());
        }
      }
    }
    return ret.toArray(Score[]::new);
  }

  public double getScore(String id) {
    return getIntScore(id).getValue();
  }

  public double removeScore(String id) {
    double ret = getIntScore(id).getValue();
    scores.remove(id);
    return ret;
  }

  public void revertQuestion(String questionId) {
    synchronized (scores) {
      for (Score s : scores.values()) {
        s.revertQuestion(questionId);
      }
    }
  }

  public void addQuestionScore(String scoreId, String questionId, ScoreType t, double value) {
    getIntScore(scoreId).addQuestionScore(questionId, new ScoreValue(t, value));
  }

  public void reset() {
    reset(null);
  }

  public void reset(String scoreId) {
    if (scoreId == null) {
      synchronized (scores) {
        for (Score s : scores.values()) {
          s.reset();
        }
      }
    } else {
      getIntScore(scoreId).reset();
    }
  }
}
