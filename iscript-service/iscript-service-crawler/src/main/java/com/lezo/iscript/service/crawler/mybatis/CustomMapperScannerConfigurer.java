package com.lezo.iscript.service.crawler.mybatis;

import org.mybatis.spring.mapper.MapperFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;

public class CustomMapperScannerConfigurer extends MapperScannerConfigurer {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        super.postProcessBeanDefinitionRegistry(registry);
        String[] nameStrings = registry.getBeanDefinitionNames();
        String className = MapperFactoryBean.class.getName();
        for (String beanName : nameStrings) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
            if (beanDefinition instanceof GenericBeanDefinition && className.equals(beanDefinition.getBeanClassName())) {
                GenericBeanDefinition definition = (GenericBeanDefinition) beanDefinition;
                definition.setBeanClass(CustomMapperFactoryBean.class);
            }
        }
    }

}
