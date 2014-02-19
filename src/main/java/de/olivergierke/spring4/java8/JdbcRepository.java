/*
 * Copyright 2013 the original author or authors.
 *
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
 */
package de.olivergierke.spring4.java8;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Oliver Gierke
 */
class JdbcRepository {

	private final JdbcTemplate template;

	/**
	 * @param dataSource
	 */
	public JdbcRepository(DataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
	}

	public void executeQueryWithPreparedStatementAndRowMapper() {

		template.query("SELECT name, age FROM person WHERE dep = ?",
			ps -> ps.setString(1, "Sales"),
			(rs, rowNum) -> new Person(rs.getString(1), rs.getString(2)));

		// VS.

		template.query("SELECT name, age FROM person WHERE dep = ?",
			new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, "Sales");
				}
			},
			new RowMapper<Person>() {
				@Override
				public Person mapRow(ResultSet rs, int i) throws SQLException {
					return new Person(rs.getString(1), rs.getString(2));
				}
			}
		);
	}

	public void executeQueryWithPreparedStatementAndMethodReference() {

		template.query("SELECT name, age FROM person WHERE dep = ?",
				ps -> ps.setString(1, "Sales"),
				this::mapPerson);
	}

	// RowMapper
	private Person mapPerson(ResultSet rs, int rowNum) throws SQLException {
		return new Person(rs.getString(1), rs.getString(2));
	}

	static class Person {

		public Person(String firstname, String lastname) {

		}
	}
}
