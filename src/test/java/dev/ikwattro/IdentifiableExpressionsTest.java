package dev.ikwattro;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.neo4j.cypherdsl.core.AliasedExpression;
import org.neo4j.cypherdsl.core.SymbolicName;
import org.neo4j.cypherdsl.parser.CypherParser;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class IdentifiableExpressionsTest {

    @ParameterizedTest
    @MethodSource("queriesToTest")
    void testGetIdentifiableElementsFromQuery(String query) {
        var statement = CypherParser.parse(query);
        var elements = statement.getIdentifiableExpressions().stream().map(e -> {
            if (e instanceof AliasedExpression) {
                return ((AliasedExpression) e).getAlias();
            }
            return ((SymbolicName) e).getValue();
        }).toList();
        assertThat(elements.size()).isGreaterThan(1);
    }

    private static Stream<Arguments> queriesToTest() {
        var fields = Arrays.stream(TestQueries.class.getDeclaredFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()))
                .toList();

        return fields.stream().map(f -> {
            try {
                return Arguments.of(f.get(String.class.getDeclaredConstructor().newInstance()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static class TestQueries {
        public static String QUERY1 = """
            MATCH (n)-[:PING_EVENT]->(e)
                    WITH n, e WHERE e.date CONTAINS "-"
                    WITH n, e, date(e.date) AS date
                    WITH n, e ORDER BY date
                    WITH n, head(collect(e)) AS event
                    RETURN id(n) AS id, datetime(event.date + 'T23:59:59Z') AS lastSeenDate
            """;

        public static String QUERY2 = """
            MATCH (p:Person)
                where id(p) = $id
                with p
                MATCH (p)-[:KNOWS]-(kp:Person)
                WITH p, count(distinct kp) AS knows_people_count
                MATCH (p)-[:KNOWS]-(kpc:Person)-[:PARTY_TO]->(:Crime)
                WITH p, knows_people_count, count(distinct kpc) as knows_criminals_count
                WITH p, toInteger((100*knows_criminals_count/toFloat(knows_people_count))) as score
                                
                CALL apoc.when(
                EXISTS((p)-[:PARTY_TO]->(:Crime)),
                'RETURN 100 as score',
                'RETURN score as score',
                 {p:p, score: score}) YIELD value
                                
                return id(p) as id,  value.score as criminalRiskScore
            """;

        public static String QUERY3 = """
            MATCH (p:Person)
                where id(p) = $id
                with p
                MATCH (p)-[:KNOWS]-(kp:Person)
                WITH p, count(distinct kp) AS knows_people_count
                MATCH (p)-[:KNOWS]-(kpc:Person)-[:PARTY_TO]->(:Crime)
                WITH p, knows_people_count, count(distinct kpc) as knows_criminals_count
                WITH p, toInteger((100*knows_criminals_count/toFloat(knows_people_count))) as score
                return id(p) as id,  score as criminalRiskScore
            """;

        public static String QUERY4 = """
            MATCH (p:Person)-[:PARTY_TO]->(c:Crime)
                WHERE id(p) = $id
                RETURN $id AS id, True AS criminal
            """;

        public static String QUERY5 = """
            match (l:Location)<-[]-(c:Crime)<-[]-(v:Vehicle)
                where id(v) = $id
                return id(v) as id, l.geospatial as location
            """;

        public static String QUERY6 = """
                MATCH (n) WHERE id(n) = $id
                RETURN n.name STARTS WITH 'Ar' AS badLink, $id AS id
                """;

        public static String QUERY7 = """
                MATCH (k:Keyword)-[:DESCRIBES]->(a)
                WHERE id(k) = $id
                WITH k, collect(DISTINCT a.time) AS timestamps
                RETURN timestamps, id(k) as id
                """;

        public static String QUERY8 = """
                MATCH (k:Keyword)-[:DESCRIBES]->(a)
                WHERE id(k) = $id
                WITH k, size(collect(DISTINCT a.time)) AS numberOfRoles, [1, 2, 3] AS roles
                RETURN apoc.coll.randomItems(roles, numberOfRoles, true) AS roles, id(k) AS id
                """;

        public static String QUERY9 = """
                MATCH (n) WHERE id(n) = $id + 10000
                RETURN id(n) AS id, 'Reviewed' IN labels(n) AS reviewed
                """;

        public static String QUERY10 = """
                MATCH (n) WHERE id(n) = $id
                RETURN $id AS id, COUNT { (n)-[:INVESTED]->() } AS totalInvestments
                """;

        public static String QUERY11 = """
                MATCH (n:Person)-[:HAS_PHONE]->(p:Phone)
                where id(p) = $id
                return id(p) as id, n.full_name as owner
                """;

        public static String QUERY12 = """
                MATCH (e:Entity)-[:IN_CLUSTER]->(c)
                WHERE id(c) = $id
                RETURN $id as id, point({latitude: avg(e.latitude), longitude:avg(e.longitude)}) as coordinates_cluster
                """;

        public static String QUERY13 = """
                MATCH (o)<-[:ORG_GROUP]-(:Organization)<-[:AWARDED_TO]-(g:Grant)
                WHERE id(o)= $id
                WITH distinct g, o
                RETURN id(o) as id, sum(g.amount) as `grant_amount`
                """;

        public static String QUERY14 = """
                MATCH (p:Person)-[:LIVES_IN]->(l:Location)
                WHERE id(p) = $id
                RETURN $id AS id, p.pleasant_temperature_threshold <= l.ga_day_temp as IsTemperatureOK
                """;

        public static String QUERY15 = """
                MATCH (p:Person) WHERE p.email = $node.values.email
                MATCH (c:Certification) WHERE c.name="Neo4j Certified Professional"
                RETURN $id as id, exists((p)-[:HAS_CERTIFICATION]->(c)) as neoCertified
                """;

        public static String QUERY16 = """
                MATCH (n) WHERE id(n) = $id
                RETURN 'https://www.google.com/search?q=' + replace(n.name,' ','+') AS googleSearchUrl, $id AS id
                """;
    }
}
