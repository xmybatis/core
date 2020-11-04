package com.github.xmybatis.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.xmybatis.core.annotation.Column;
import com.github.xmybatis.core.annotation.LeftJoin;
import com.github.xmybatis.core.annotation.Table;

public abstract class SQLProvider {

	Logger log = LoggerFactory.getLogger(SQLProvider.class);

	private Class<? extends Entity> entityClass;

	// private T entityClass;
	protected SQLProvider() {

	}

	public SQLProvider(Class<? extends Entity> entityClass) {
		this.entityClass = entityClass;
	}

	private String getFullTableName(Entity info) {
		String tableName = ((Table) info.getClass().getAnnotation(Table.class)).value();
		return info.getSchema() == null ? tableName : String.format("%s.%s", info.getSchema(), tableName);
	}

	public String insertSelective(Entity info)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// Class clz = info.getClass();
		// Table t = (Table) entityClass.getAnnotation(Table.class);

		// String tableName = t.value();

		SQL sql = new SQL();
		sql.INSERT_INTO(getFullTableName(info));

		Field[] fields = info.getClass().getDeclaredFields();
		for (Field f : fields) {

			Annotation methodAnnon = f.getAnnotation(Column.class);
			if (methodAnnon == null) {
				continue;
			}

			Column c = (Column) methodAnnon;
			if (c.readOnly()) {
				continue;
			}

			f.setAccessible(true);

			Object value = f.get(info);
			
			// if (c.id()) {
				// continue;
			// }
			
			if (value != null) {
				// log.info("column:"+c.value());
				sql.INTO_COLUMNS(getAfterDot(c.value()));

				sql.INTO_VALUES("#{" + f.getName() + "}");
			}
		}

		return sql.toString();
	}

	public String insert(Object info)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// Class clz = info.getClass();
		Table t = (Table) entityClass.getAnnotation(Table.class);

		String tableName = t.value();
		SQL sql = new SQL();
		sql.INSERT_INTO(tableName);

		Field[] fields = entityClass.getDeclaredFields();
		for (Field f : fields) {

			Annotation methodAnnon = f.getAnnotation(Column.class);
			if (methodAnnon == null) {
				continue;
			}

			Column c = (Column) methodAnnon;
			if (c.readOnly()) {
				continue;
			}

			if (c.id()) {
				continue;
			}

			sql.INTO_COLUMNS("`" + getAfterDot(c.value()) + "`");
			// log.info("property:" + f.getName());
			sql.INTO_VALUES("#{" + f.getName() + "}");

		}

		return sql.toString();
	}

	public String updateSelectiveById(Entity info)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class clz = info.getClass();
		Table t = (Table) clz.getAnnotation(Table.class);
		SQL sql = new SQL();

		String tableName = t.value();

		sql.UPDATE(getFullTableName(info));

		Field[] fields = clz.getDeclaredFields();
		for (Field f : fields) {

			Annotation methodAnnon = f.getAnnotation(Column.class);
			if (methodAnnon == null) {
				continue;
			}

			// Annotation idAnnon = f.getAnnotation(Id.class);

			Column c = (Column) methodAnnon;
			if (c.readOnly()) {
				continue;
			}
			// Object value = m.invoke(info);
			f.setAccessible(true);
			Object value = f.get(info);
			if (!c.id()) {
				if (value != null) {

					sql.SET(String.format("`%s`=#{%s}", getAfterDot(c.value()), f.getName()));
				}
			} else {
				sql.WHERE(String.format("`%s`=#{%s}", getAfterDot(c.value()), f.getName()));

			}
		}

		return sql.toString();
	}

	public String updateSelectiveByExample(@Param("record") Entity info, @Param("example") Example example)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class clz = info.getClass();

		Table t = (Table) clz.getAnnotation(Table.class);

		SQL sql = new SQL();

		String tableName = t.value();
		sql.UPDATE(StringUtils.isBlank(info.getSchema()) ? String.format("`%s`", tableName)
				: String.format("`%s`.`%s`", info.getSchema(), tableName));

		Field[] fields = clz.getDeclaredFields();
		for (Field f : fields) {

			Annotation methodAnnon = f.getAnnotation(Column.class);
			if (methodAnnon == null) {
				continue;
			}

			// Annotation idAnnon = f.getAnnotation(Id.class);

			Column c = (Column) methodAnnon;
			if (c.readOnly()) {
				continue;
			}
			f.setAccessible(true);
			Object value = f.get(info);

			if (!c.id()) {
				if (value != null) {
					sql.SET(String.format("`%s`=#{record.%s}", getAfterDot(c.value()), f.getName()));

				}
			}
		}
		// log.info("<script>" + sql.toString() + " " + genWhere_U_D("") + "
		// </script>");
		return "<script>" + sql.toString() + " " + genWhere_U_D("") + " </script>";
	}

	protected SQL selectSub(SQL sql) {
		Field[] fields = entityClass.getDeclaredFields();
		for (Field f : fields) {

			Annotation methodAnnon = f.getAnnotation(Column.class);
			if (methodAnnon == null) {
				continue;
			}

			Column c = (Column) methodAnnon;

			if (c.alias().length() > 0) {
				sql.SELECT(String.format("%s AS `%s`", dealColumnName(c.value()), c.alias()));
			} else {
				sql.SELECT(String.format("%s AS `%s`", dealColumnName(c.value()), f.getName()));
			}

		}
		return sql;
	}

	protected SQL fromSub(SQL sql) {
		return fromSub(null, sql);
	}

	protected SQL fromSub(String schema, SQL sql) {
		Table t = (Table) entityClass.getAnnotation(Table.class);

		String tableName = t.value();

		// log.info("t.alias:"+t.alias());
		if (StringUtils.isBlank(t.alias())) {
			// log.info("no alias");
			sql.FROM(StringUtils.isBlank(schema) ? tableName : String.format("%s.%s", schema, tableName));
		} else {
			sql.FROM(StringUtils.isBlank(schema) ? String.format("%s AS %s", tableName, t.alias())
					: String.format("%s.%s AS %s", schema, tableName, t.alias()));
		}
		LeftJoin[] joins = t.leftJoin();

		for (LeftJoin j : joins) {
			if (j.joinTable().contains(".")) {
				sql.LEFT_OUTER_JOIN(
						j.joinTable() + (StringUtils.isBlank(j.as()) ? "" : " AS " + j.as()) + " ON " + j.on());
				continue;
			}
			sql.LEFT_OUTER_JOIN(
					(StringUtils.isBlank(schema) ? j.joinTable() : String.format("%s.%s", schema, j.joinTable()))
							+ (StringUtils.isBlank(j.as()) ? "" : " AS " + j.as()) + " ON " + j.on());
		}

		return sql;
	}

	protected SQL whereByExampleSub(SQL sql) {
		return sql;
	}

	public String selectById(@Param("schema") final String schema, @Param("id") Integer id) {

		SQL sql = new SQL();

		selectSub(sql);
		fromSub(schema, sql);

		Field[] fields = entityClass.getDeclaredFields();
		for (Field f : fields) {
			Annotation colAnnon = f.getAnnotation(Column.class);
			if (colAnnon == null) {
				continue;
			}
			Column c = (Column) colAnnon;
			if (!c.id()) {
				continue;
			}
			sql.WHERE(c.value() + "=#{id}");
		}

		return sql.toString();
	}

//	public String selectByDbAndExample(String dbName,Example ex) {
//
//		return innerSelectByExample(dbName,ex,"");
//	}
	public String selectByExample(Example ex) {

		return innerSelectByExample(ex, "");
	}

	protected String innerSelectByExample(Example ex, String exWhere) {

		SQL sql = new SQL();

		selectSub(sql);
		if (ex.getSchema() == null) {
			fromSub(sql);
		} else {
			fromSub(ex.getSchema(), sql);
		}

		if (ex.isDistinct()) {
			sql.SELECT_DISTINCT("");
		}

		String limit = " ";
		if (ex.getLimit() != null) {
			limit = " LIMIT " + ex.getLimit() + " ";
		}

		if (ex.getOffset() != null) {
			limit += " OFFSET  " + ex.getOffset() + " ";
		}
		String sqlText = "<script>\r\n" + sql.toString() + genWhere(exWhere)

				+ ex.getOrderByClause() + " " + limit + " \r\n</script>";
		// log.info(sqlText);
		return sqlText;
	}

//	public String countByDbAndExample(String dbName,Example ex) {
//
//
//		return innerCountByExample(dbName,ex,"");
//	}
	public String countByExample(Example ex) {

		return innerCountByExample(ex, "");
	}

	protected String innerCountByExample(Example ex, String exWhere) {

		SQL sql = new SQL();

		sql.SELECT("count(1)");
		if (ex.getSchema() == null) {
			fromSub(sql);
		} else {
			fromSub(ex.getSchema(), sql);
		}

		return "<script>" + sql.toString() + genWhere(exWhere) + " </script>";
	}

	public String deleteById(@Param("schema") final String schema, @Param("id") int id) {

		Table t = (Table) entityClass.getAnnotation(Table.class);
		SQL sql = new SQL();
		String fromTable = t.value();
		if (fromTable.indexOf(".") > -1 || StringUtils.isBlank(schema)) {
			
			fromTable = String.format("`%s`", fromTable);
		} else {
			fromTable = String.format("`%s`.`%s`", schema, fromTable);
		}
		sql.DELETE_FROM(fromTable);

		Field[] fields = entityClass.getDeclaredFields();
		for (Field f : fields) {

			Annotation methodAnnon = f.getAnnotation(Column.class);
			if (methodAnnon == null) {
				continue;
			}

			Column c = (Column) methodAnnon;

			if (c.id()) {
				sql.WHERE(getAfterDot(c.value()) + "=#{id}");
			}

		}
		return sql.toString();
	}

	public String deleteByExample(Example ex) {

		Table t = (Table) entityClass.getAnnotation(Table.class);
		SQL sql = new SQL();

		sql.DELETE_FROM(t.value());

		return "<script>" + sql.toString() + " " + genWhere("") + " </script>";
	}

	public String existsByExample(Example ex) {
		Table t = (Table) entityClass.getAnnotation(Table.class);
		// select exists(select 1 from ne where name='ss')
		return "<script>" + "select exists(select 1 from " + t.value() + genWhere("") + " ) </script>";
	}

	private String genWhere(String exSQLText) {
		String text = (exSQLText != null && !StringUtils.isBlank(exSQLText)) ? (" and (" + exSQLText + ") ") : "";
		return " <where>\r\n" + where0 + text + "\r\n </where>";
	}

	private String genWhere_U_D(String exSQLText) {
		String text = (exSQLText != null && !StringUtils.isBlank(exSQLText)) ? (" and (" + exSQLText + ") ") : "";
		return " <where>\r\n" + where_u_d + text + "\r\n </where>";
	}

	protected static final String where0 = "      <foreach collection=\"oredCriteria\" item=\"criteria\" separator=\" or \">\r\n"
			+ "        <if test=\"criteria.valid\">\r\n"
			+ "          <trim prefix=\"(\" prefixOverrides=\"and\" suffix=\")\">\r\n"
			+ "            <foreach collection=\"criteria.criteria\" item=\"criterion\">\r\n"
			+ "              <choose>\r\n" + "                <when test=\"criterion.noValue\">\r\n"
			+ "                  and ${criterion.condition}\r\n" + "                </when>\r\n"
			+ "                <when test=\"criterion.singleValue\">\r\n"
			+ "                  and ${criterion.condition} #{criterion.value} ${criterion.suffix}\r\n"
			+ "                </when>\r\n" + "                <when test=\"criterion.likeValue\">\r\n"
			+ "                  and ${criterion.condition} concat('${criterion.likePrefix}', #{criterion.value}, '${criterion.likeSuffix}')\r\n"
			+ "                </when>\r\n" + "                <when test=\"criterion.betweenValue\">\r\n"
			+ "                  and ${criterion.condition} #{criterion.value} and #{criterion.secondValue}\r\n"
			+ "                </when>\r\n" + "                <when test=\"criterion.listValue\">\r\n"
			+ "                  and ${criterion.condition}\r\n"
			+ "                  <foreach close=\")\" collection=\"criterion.value\" item=\"listItem\" open=\"(\" separator=\",\">\r\n"
			+ "                    #{listItem}\r\n" + "                  </foreach>\r\n" + "                </when>\r\n"
			+ "              </choose>\r\n" + "            </foreach>\r\n" + "          </trim>\r\n"
			+ "        </if>\r\n" + "      </foreach>\r\n";

	protected static final String where_u_d = "      <foreach collection=\"example.oredCriteria\" item=\"criteria\" separator=\" or \">\r\n"
			+ "        <if test=\"criteria.valid\">\r\n"
			+ "          <trim prefix=\"(\" prefixOverrides=\"and\" suffix=\")\">\r\n"
			+ "            <foreach collection=\"criteria.criteria\" item=\"criterion\">\r\n"
			+ "              <choose>\r\n" + "                <when test=\"criterion.noValue\">\r\n"
			+ "                  and ${criterion.condition}\r\n" + "                </when>\r\n"
			+ "                <when test=\"criterion.singleValue\">\r\n"
			+ "                  and ${criterion.condition} #{criterion.value} ${criterion.suffix}\r\n"
			+ "                </when>\r\n" + "                <when test=\"criterion.likeValue\">\r\n"
			+ "                  and ${criterion.condition} concat('${criterion.likePrefix}', #{criterion.value}, '${criterion.likeSuffix}')\r\n"
			+ "                </when>\r\n" + "                <when test=\"criterion.betweenValue\">\r\n"
			+ "                  and ${criterion.condition} #{criterion.value} and #{criterion.secondValue}\r\n"
			+ "                </when>\r\n" + "                <when test=\"criterion.listValue\">\r\n"
			+ "                  and ${criterion.condition}\r\n"
			+ "                  <foreach close=\")\" collection=\"criterion.value\" item=\"listItem\" open=\"(\" separator=\",\">\r\n"
			+ "                    #{listItem}\r\n" + "                  </foreach>\r\n" + "                </when>\r\n"
			+ "              </choose>\r\n" + "            </foreach>\r\n" + "          </trim>\r\n"
			+ "        </if>\r\n" + "      </foreach>\r\n";

	private String getAfterDot(String s) {
		int i;
		if ((i = s.indexOf('.')) > -1) {
			return s.substring(i + 1);
		}
		return s;
	}

	public String dealColumnName(String v) {
		String[] vv = v.split("\\.");
		if (vv.length == 2) {
			return String.format("`%s`.`%s`", vv[0], vv[1]);
		}
		return String.format("`%s`", v);

	}
}
