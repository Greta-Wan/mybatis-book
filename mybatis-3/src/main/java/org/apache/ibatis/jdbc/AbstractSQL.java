/**
 *    Copyright 2009-2017 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.jdbc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Clinton Begin
 * @author Jeff Butler
 * @author Adam Gent
 * @author Kazuki Shimizu
 */
public abstract class AbstractSQL<T> {

  private static final String AND = ") \nAND (";
  private static final String OR = ") \nOR (";

  private final SQLStatement sql = new SQLStatement();

  public abstract T getSelf();

  public T UPDATE(String table) {
    sql().statementType = SQLStatement.StatementType.UPDATE;
    sql().tables.add(table);
    return getSelf();
  }

  public T SET(String sets) {
    sql().sets.add(sets);
    return getSelf();
  }

  /**
   * @since 3.4.2
   */
  public T SET(String... sets) {
    sql().sets.addAll(Arrays.asList(sets));
    return getSelf();
  }

  public T INSERT_INTO(String tableName) {
    sql().statementType = SQLStatement.StatementType.INSERT;
    sql().tables.add(tableName);
    return getSelf();
  }

  public T VALUES(String columns, String values) {
    sql().columns.add(columns);
    sql().values.add(values);
    return getSelf();
  }

  /**
   * @since 3.4.2
   */
  public T INTO_COLUMNS(String... columns) {
    sql().columns.addAll(Arrays.asList(columns));
    return getSelf();
  }

  /**
   * @since 3.4.2
   */
  public T INTO_VALUES(String... values) {
    sql().values.addAll(Arrays.asList(values));
    return getSelf();
  }

  public T SELECT(String columns) {
    sql().statementType = SQLStatement.StatementType.SELECT;
    sql().select.add(columns);
    return getSelf();
  }

  /**
   * @since 3.4.2
   */
  public T SELECT(String... columns) {
    sql().statementType = SQLStatement.StatementType.SELECT;
    sql().select.addAll(Arrays.asList(columns));
    return getSelf();
  }

  public T SELECT_DISTINCT(String columns) {
    sql().distinct = true;
    SELECT(columns);
    return getSelf();
  }

  /**
   * @since 3.4.2
   */
  public T SELECT_DISTINCT(String... columns) {
    sql().distinct = true;
    SELECT(columns);
    return getSelf();
  }

  public T DELETE_FROM(String table) {
    sql().statementType = SQLStatement.StatementType.DELETE;
    sql().tables.add(table);
    return getSelf();
  }

  public T FROM(String table) {
    sql().tables.add(table);
    return getSelf();
  }

  /**
   * @since 3.4.2
   */
  public T FROM(String... tables) {
    sql().tables.addAll(Arrays.asList(tables));
    return getSelf();
  }

  public T JOIN(String join) {
    sql().join.add(join);
    return getSelf();
  }

  /**
   * @since 3.4.2
   */
  public T JOIN(String... joins) {
    sql().join.addAll(Arrays.asList(joins));
    return getSelf();
  }

  public T INNER_JOIN(String join) {
    sql().innerJoin.add(join);
    return getSelf();
  }

  /**
   * @since 3.4.2
   */
  public T INNER_JOIN(String... joins) {
    sql().innerJoin.addAll(Arrays.asList(joins));
    return getSelf();
  }

  public T LEFT_OUTER_JOIN(String join) {
    sql().leftOuterJoin.add(join);
    return getSelf();
  }

  /**
   * @since 3.4.2
   */
  public T LEFT_OUTER_JOIN(String... joins) {
    sql().leftOuterJoin.addAll(Arrays.asList(joins));
    return getSelf();
  }

  public T RIGHT_OUTER_JOIN(String join) {
    sql().rightOuterJoin.add(join);
    return getSelf();
  }

  /**
   * @since 3.4.2
   */
  public T RIGHT_OUTER_JOIN(String... joins) {
    sql().rightOuterJoin.addAll(Arrays.asList(joins));
    return getSelf();
  }

  public T OUTER_JOIN(String join) {
    sql().outerJoin.add(join);
    return getSelf();
  }

  /**
   * @since 3.4.2
   */
  public T OUTER_JOIN(String... joins) {
    sql().outerJoin.addAll(Arrays.asList(joins));
    return getSelf();
  }

  public T WHERE(String conditions) {
    sql().where.add(conditions);
    sql().lastList = sql().where;
    return getSelf();
  }

  /**
   * @since 3.4.2
   */
  public T WHERE(String... conditions) {
    sql().where.addAll(Arrays.asList(conditions));
    sql().lastList = sql().where;
    return getSelf();
  }

  public T OR() {
    sql().lastList.add(OR);
    return getSelf();
  }

  public T AND() {
    sql().lastList.add(AND);
    return getSelf();
  }

  public T GROUP_BY(String columns) {
    sql().groupBy.add(columns);
    return getSelf();
  }

  /**
   * @since 3.4.2
   */
  public T GROUP_BY(String... columns) {
    sql().groupBy.addAll(Arrays.asList(columns));
    return getSelf();
  }

  public T HAVING(String conditions) {
    sql().having.add(conditions);
    sql().lastList = sql().having;
    return getSelf();
  }

  /**
   * @since 3.4.2
   */
  public T HAVING(String... conditions) {
    sql().having.addAll(Arrays.asList(conditions));
    sql().lastList = sql().having;
    return getSelf();
  }

  public T ORDER_BY(String columns) {
    sql().orderBy.add(columns);
    return getSelf();
  }

  /**
   * @since 3.4.2
   */
  public T ORDER_BY(String... columns) {
    sql().orderBy.addAll(Arrays.asList(columns));
    return getSelf();
  }

  private SQLStatement sql() {
    return sql;
  }

  public <A extends Appendable> A usingAppender(A a) {
    sql().sql(a);
    return a;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    // 调用SQLStatement对象的sql()方法生成SQL语句
    sql().sql(sb);
    return sb.toString();
  }

  /**
   * 工具类
   */
  private static class SafeAppendable {
    private final Appendable a;
    private boolean empty = true;

    public SafeAppendable(Appendable a) {
      super();
      this.a = a;
    }

    public SafeAppendable append(CharSequence s) {
      try {
        if (empty && s.length() > 0) {
          empty = false;
        }
        a.append(s);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      return this;
    }

    public boolean isEmpty() {
      return empty;
    }

  }

  private static class SQLStatement {
    // SQL语句的类型
    public enum StatementType {
      DELETE, INSERT, SELECT, UPDATE
    }

    StatementType statementType;
    // 用于记录SQL实例SELECT()、UPDATE()等方法调用参数
    List<String> sets = new ArrayList<String>();
    List<String> select = new ArrayList<String>();
    List<String> tables = new ArrayList<String>();
    List<String> join = new ArrayList<String>();
    List<String> innerJoin = new ArrayList<String>();
    List<String> outerJoin = new ArrayList<String>();
    List<String> leftOuterJoin = new ArrayList<String>();
    List<String> rightOuterJoin = new ArrayList<String>();
    List<String> where = new ArrayList<String>();
    List<String> having = new ArrayList<String>();
    List<String> groupBy = new ArrayList<String>();
    List<String> orderBy = new ArrayList<String>();
    List<String> lastList = new ArrayList<String>();
    List<String> columns = new ArrayList<String>();
    List<String> values = new ArrayList<String>();
    // 是否包含distinct关键字
    boolean distinct;

    public SQLStatement() {
        // Prevent Synthetic Access
    }

    /**
     * SQL语句拼接
     * @param builder SQL字符串构建对象
     * @param keyword SQL关键字
     * @param parts SQL关键字子句内容
     * @param open SQL关键字后开始字符
     * @param close SQL关键字后结束字符
     * @param conjunction SQL连接关键字，通常为AND或OR
     */
    private void sqlClause(SafeAppendable builder, String keyword, List<String> parts, String open, String close,
                           String conjunction) {
      if (!parts.isEmpty()) {
        if (!builder.isEmpty()) {
          builder.append("\n");
        }
        // 拼接SQL关键字
        builder.append(keyword);
        builder.append(" ");
        // 拼接关键字后开始字符
        builder.append(open);
        String last = "________";
        for (int i = 0, n = parts.size(); i < n; i++) {
          String part = parts.get(i);
          // 如果SQL关键字对应的子句内容不为OR或AND,则追加连接关键字
          if (i > 0 && !part.equals(AND) && !part.equals(OR) && !last.equals(AND) && !last.equals(OR)) {
            builder.append(conjunction);
          }
          // 追加子句内容
          builder.append(part);
          last = part;
        }
        // 追加关键字后结束字符
        builder.append(close);
      }
    }

    private String selectSQL(SafeAppendable builder) {
      if (distinct) {
        sqlClause(builder, "SELECT DISTINCT", select, "", "", ", ");
      } else {
        sqlClause(builder, "SELECT", select, "", "", ", ");
      }

      sqlClause(builder, "FROM", tables, "", "", ", ");
      joins(builder);
      sqlClause(builder, "WHERE", where, "(", ")", " AND ");
      sqlClause(builder, "GROUP BY", groupBy, "", "", ", ");
      sqlClause(builder, "HAVING", having, "(", ")", " AND ");
      sqlClause(builder, "ORDER BY", orderBy, "", "", ", ");
      return builder.toString();
    }

    private void joins(SafeAppendable builder) {
      sqlClause(builder, "JOIN", join, "", "", "\nJOIN ");
      sqlClause(builder, "INNER JOIN", innerJoin, "", "", "\nINNER JOIN ");
      sqlClause(builder, "OUTER JOIN", outerJoin, "", "", "\nOUTER JOIN ");
      sqlClause(builder, "LEFT OUTER JOIN", leftOuterJoin, "", "", "\nLEFT OUTER JOIN ");
      sqlClause(builder, "RIGHT OUTER JOIN", rightOuterJoin, "", "", "\nRIGHT OUTER JOIN ");
    }

    private String insertSQL(SafeAppendable builder) {
      sqlClause(builder, "INSERT INTO", tables, "", "", "");
      sqlClause(builder, "", columns, "(", ")", ", ");
      sqlClause(builder, "VALUES", values, "(", ")", ", ");
      return builder.toString();
    }

    private String deleteSQL(SafeAppendable builder) {
      sqlClause(builder, "DELETE FROM", tables, "", "", "");
      sqlClause(builder, "WHERE", where, "(", ")", " AND ");
      return builder.toString();
    }

    private String updateSQL(SafeAppendable builder) {
      sqlClause(builder, "UPDATE", tables, "", "", "");
      joins(builder);
      sqlClause(builder, "SET", sets, "", "", ", ");
      sqlClause(builder, "WHERE", where, "(", ")", " AND ");
      return builder.toString();
    }

    public String sql(Appendable a) {
      SafeAppendable builder = new SafeAppendable(a);
      if (statementType == null) {
        return null;
      }

      String answer;

      switch (statementType) {
        case DELETE:
          answer = deleteSQL(builder);
          break;

        case INSERT:
          answer = insertSQL(builder);
          break;

        case SELECT:
          answer = selectSQL(builder);
          break;

        case UPDATE:
          answer = updateSQL(builder);
          break;

        default:
          answer = null;
      }

      return answer;
    }
  }
}