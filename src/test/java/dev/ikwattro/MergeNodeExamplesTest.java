package dev.ikwattro;

import org.junit.jupiter.api.Test;
import org.neo4j.cypherdsl.core.Cypher;

import java.util.Map;

public class MergeNodeExamplesTest extends AbstractCypherDSLTest {

    @Test
    void merge_node_with_label_and_property() {
        var statement = Cypher.merge(
                Cypher
                        .node("Person")
                        .named("person")
                        .withProperties(Map.of("name", "john"))
        ).build();
        generateQuery(statement);
//        MERGE (person:`Person` {
//            name: 'john'
//        })
    }
}
