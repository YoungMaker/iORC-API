package edu.ycp.cs482.iorcapi.error;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.List;


//TODO: This exception does not get inserted into the graphql response payload. Fix.
public class QueryException extends  RuntimeException implements GraphQLError {

    private String message;
    private ErrorType errorType;

    public QueryException(String message, ErrorType errorType) {
        super(message);
        this.message = message;
        this.errorType = errorType;
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorType getErrorType() {
        return errorType;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
