package com.stockflow.core.config;

import com.stockflow.core.tenant.domain.Tenant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class ApplicationContext {

    private static final ThreadLocal<ApplicationContext> CURRENT = ThreadLocal.withInitial(ApplicationContext::new);

    private Tenant currentTenant;

    public static ApplicationContext get() {
        return CURRENT.get();
    }

    public static void set(ApplicationContext context) {
        CURRENT.set(context);
    }

    public static void clear() {
        CURRENT.remove();
    }

    public static Optional<Tenant> getCurrentTenant() {
        return Optional.ofNullable(get().currentTenant);
    }

    public static void setCurrentTenant(Tenant tenant) {
        ApplicationContext ctx = get();
        ctx.currentTenant = tenant;
        set(ctx);
    }

}