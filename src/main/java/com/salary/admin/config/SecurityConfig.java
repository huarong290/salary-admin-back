package com.salary.admin.config;


import com.salary.admin.filter.JwtAuthenticationFilter;
import com.salary.admin.property.SecurityWhiteListProperties;
import com.salary.admin.security.JwtAccessDeniedHandler;
import com.salary.admin.security.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final SecurityWhiteListProperties securityWhiteListProperties; // 注入 YAML 配置类

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 将 List 转为数组，方便 Security 调用
        String[] whitelist = securityWhiteListProperties.getWhitelist().toArray(new String[0]);

        return http
                // 1. 禁用不需要的特性
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                // 2. 无状态 Session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. 核心权限配置：动态加载白名单
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(whitelist).permitAll() // 动态注入！
                        .anyRequest().authenticated()
                )

                // 4. 异常处理
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)// 挂载 401
                        .accessDeniedHandler(jwtAccessDeniedHandler)// 挂载 403
                )

                // 5. 过滤器顺序：在 UsernamePasswordAuthenticationFilter 之前执行 JWT 校验
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}
