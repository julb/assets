package me.julb.springbootstarter.consumer.encoders;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;

/**
 * The custom pageable encoder fixing sort issues.
 * <P>
 * The difference with PageableSpringEncoder is the "," translated to "%2C".
 * <P>
 * See https://github.com/spring-cloud/spring-cloud-openfeign/issues/440.
 * <P>
 * @author Julb.
 */
public class CustomPageableSpringEncoder implements Encoder {

    /**
     * The delegate attribute.
     */
    private final Encoder delegate;

    /**
     * Page index parameter name.
     */
    private static final String PAGE_PARAMETER = "page";

    /**
     * Page size parameter name.
     */
    private static final String SIZE_PARAMETER = "size";

    /**
     * Sort parameter name.
     */
    private static final String SORT_PARAMETER = "sort";

    /**
     * Creates a new PageableSpringEncoder with the given delegate for fallback. If no delegate is provided and this encoder cant handle the request, an EncodeException is thrown.
     * @param delegate The optional delegate.
     */
    public CustomPageableSpringEncoder(Encoder delegate) {
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template)
        throws EncodeException {
        if (supports(object)) {
            if (object instanceof Pageable) {
                Pageable pageable = (Pageable) object;

                if (pageable.isPaged()) {
                    template.query(PAGE_PARAMETER, pageable.getPageNumber() + "");
                    template.query(SIZE_PARAMETER, pageable.getPageSize() + "");
                }

                applySort(template, pageable.getSort());
            } else if (object instanceof Sort) {
                Sort sort = (Sort) object;
                applySort(template, sort);
            }
        } else {
            if (delegate != null) {
                delegate.encode(object, bodyType, template);
            } else {
                throw new EncodeException("PageableSpringEncoder does not support the given object " + object.getClass() + " and no delegate was provided for fallback!");
            }
        }
    }

    /**
     * Apply encoding for {@link Sort} definition.
     * @param template the template.
     * @param sort the sort.
     */
    private void applySort(RequestTemplate template, Sort sort) {
        Collection<String> existingSorts = template.queries().get(SORT_PARAMETER);
        List<String> sortQueries = existingSorts != null ? new ArrayList<>(existingSorts) : new ArrayList<>();
        for (Sort.Order order : sort) {
            sortQueries.add(order.getProperty() + "%2C" + order.getDirection());
        }
        if (!sortQueries.isEmpty()) {
            template.query(SORT_PARAMETER, sortQueries);
        }
    }

    /**
     * Returns <code>true</code> if the object should be processed by this encoder.
     * @param object the object.
     * @return <code>true</code> if the object should be processed by this encoder.
     */
    protected boolean supports(Object object) {
        return object instanceof Pageable || object instanceof Sort;
    }
}