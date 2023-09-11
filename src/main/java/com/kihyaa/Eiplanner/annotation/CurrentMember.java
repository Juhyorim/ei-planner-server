package com.kihyaa.Eiplanner.annotation;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@AuthenticationPrincipal(expression = "#this instanceof T(com.kihyaa.Eiplanner.security.wrapper.MemberWrapper) ? #this.member : null")
public @interface CurrentMember {
}

