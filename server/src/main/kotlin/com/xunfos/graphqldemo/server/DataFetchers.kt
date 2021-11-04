package com.xunfos.graphqldemo.server

import com.expediagroup.graphql.generator.execution.SimpleKotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.generator.extensions.deepName
import com.expediagroup.graphql.generator.extensions.unwrapType
import com.fasterxml.jackson.databind.ObjectMapper
import graphql.schema.DataFetcher
import graphql.schema.DataFetcherFactory
import graphql.schema.DataFetcherFactoryEnvironment
import org.springframework.beans.factory.BeanFactory
import org.springframework.stereotype.Component
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

@Component
class SpringDataFetcherFactory(
    private val beanFactory: BeanFactory,
) : DataFetcherFactory<Any?> {

    @Suppress("UNCHECKED_CAST")
    override fun get(environment: DataFetcherFactoryEnvironment?): DataFetcher<Any?> {
        // Strip out possible `Input` and `!` suffixes added to by the SchemaGenerator
        val targetedTypeName =
            environment?.fieldDefinition?.type?.unwrapType()?.deepName
                ?.removeSuffix("!")
                ?.removeSuffix("Input")
                ?: ""

        return beanFactory.getBean("${targetedTypeName}DataFetcher") as DataFetcher<Any?>
    }
}

/**
 * Custom DataFetcherFactory provider that returns custom Spring based DataFetcherFactory for resolving lateinit properties.
 */
@Component
class CustomDataFetcherFactoryProvider(
    private val springDataFetcherFactory: SpringDataFetcherFactory,
    objectMapper: ObjectMapper,
) : SimpleKotlinDataFetcherFactoryProvider(objectMapper) {

    override fun propertyDataFetcherFactory(kClass: KClass<*>, kProperty: KProperty<*>): DataFetcherFactory<Any?> =
        if (kProperty.isLateinit) {
            springDataFetcherFactory
        } else {
            super.propertyDataFetcherFactory(kClass, kProperty)
        }
}
