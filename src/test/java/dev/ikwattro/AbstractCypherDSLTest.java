package dev.ikwattro;

import org.neo4j.cypherdsl.core.ResultStatement;
import org.neo4j.cypherdsl.core.renderer.Configuration;
import org.neo4j.cypherdsl.core.renderer.Dialect;
import org.neo4j.cypherdsl.core.renderer.Renderer;

public abstract class AbstractCypherDSLTest {

    protected String generateQuery(ResultStatement resultStatement) {
        var config = Configuration.newConfig().withPrettyPrint(true).alwaysEscapeNames(true).withDialect(Dialect.NEO4J_5).build();
        var generatedQuery = Renderer.getRenderer(config).render(resultStatement);
        System.out.println(generatedQuery);
        return generatedQuery;
    }
}
