package edu.ycp.cs482.iorcapi.error

import graphql.ExceptionWhileDataFetching
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.ExecutionPath
import graphql.language.SourceLocation
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class SimpleDataFetcherExceptionHandler : DataFetcherExceptionHandler {

    override fun accept(handlerParameters: DataFetcherExceptionHandlerParameters) {
        val exception = handlerParameters.exception
        val sourceLocation = handlerParameters.field.sourceLocation
        val path = handlerParameters.path

        val error = ExceptionWhileDataFetching(path, exception, sourceLocation)
        handlerParameters.executionContext.addError(error, path)
        log.warn(error.message + " TEST", exception)
    }

    companion object {

        private val log = LoggerFactory.getLogger(SimpleDataFetcherExceptionHandler::class.java)
    }
}
