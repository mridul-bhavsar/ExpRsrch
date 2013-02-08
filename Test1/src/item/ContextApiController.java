/**
 * ContextApiController.java		 Nov 14, 2012 
 * 
 * © 2012 Best Buy. All Rights Reserved.
 * The trademarks used in these materials are the properties of their respective
 * owners. This work is protected by copyright law and contains valuable trade
 * secrets and confidential information.
 */

package com.bestbuy.context.api.item;

import com.bestbuy.context.api.ApiConstants;
import com.bestbuy.context.api.InvalidParameterException;
import com.bestbuy.context.api.response.ContextResponse;
import com.bestbuy.context.api.response.ResponseBuilder;
import com.bestbuy.context.api.response.page.PagingAndSorting;
import com.bestbuy.context.domain.item.Item;
import com.bestbuy.context.domain.item.ItemService;
import com.bestbuy.context.domain.page.Page;
import com.bestbuy.context.util.RequestParamValidator;
import com.bestbuy.fp.api.rest.AbstractApiController;
import com.bestbuy.fp.core.exception.AbstractException;
import com.bestbuy.fp.core.exception.AbstractRuntimeException;
import com.yammer.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

/**
 * Class used to fetch API response based on Context ID.
 * API covered is <ol><li>Recently Viewed API</li><li>Recommended Items API</li></ol>
 * @author rupesh.nangalia
 *
 */
@Controller
public class ContextApiController extends AbstractApiController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContextApiController.class);

	@Autowired
	private ItemService itemService;

	@Autowired
	private ResponseBuilder responseBuilder;

	private static final String COOKIE_PATH_VARIABLE = "current";

	/**
	 * This method accepts the request from the user for Recently Viewed API and returns a {@link ContextResponse} object as response. 
	 * The response is in the form of a JSON/XML file.
	 * Mapped URL = /context/{id}/items/viewed.
	 * RequestMethod = GET.
	 * @param pageSort Defines the parameters of the response page
	 * @param uriBuilder Used for creating a URI
	 * @param pathContextId Context Id for which Recently Viewed API is called. Can be taken from URL
	 * @param cookieContextId Context Id for which Recently Viewed API is called. Can be taken from cookie
	 * @param prodInfo Parameter which can be set to either minimal or intermediate
	 * @param request HTTP request
	 * @return ContextResponse object
	 * @throws AbstractException User defined Exception
	 * @throws AbstractRuntimeException User defined Exception
	 */
	@Timed(name = "Recently Viewed")
	@RequestMapping(value = "/context/{id}/items/viewed", method = RequestMethod.GET)
	public ContextResponse<Item> getRecentItemsForContext(final PagingAndSorting pageSort, final UriComponentsBuilder uriBuilder,
			@CookieValue(value = "context_id", defaultValue = "null") final String cookieContextId,
			@PathVariable("id") final String pathContextId,
			@RequestParam(value = "productInfo", defaultValue = ApiConstants.PROD_INFO_MINIMAL) final String prodInfo,
			final HttpServletRequest request) throws AbstractException, AbstractRuntimeException {
		String contextId = pathContextId;
		if (this.LOGGER.isInfoEnabled()) {
			this.LOGGER.info("Getting context_id from URL - context_id = " + contextId);
		}
		if (this.COOKIE_PATH_VARIABLE.equals(pathContextId) && cookieContextId != null && !"null".equals(cookieContextId)) {
			contextId = cookieContextId;
		}
		if ((!RequestParamValidator.isValid(contextId))) { throw new InvalidParameterException(InvalidParameterException.INVALID_CONTEXT_ID,
				"Invalid Context Id. Context Id = " + contextId); }
		final Page<Item> page = this.itemService.findRecentlyViewedItemsForContextId(pageSort, contextId, prodInfo);

		final ContextResponse<Item> contextResponse = this.responseBuilder.responseBuilderForRecentlyViewed(page, contextId, pageSort);

		return contextResponse;
	}

	/**
	 * This method accepts the request from the user for Recommended Items API and returns a {@link ContextResponse} object as response. 
	 * The response is in the form of a JSON/XML file.
	 * Mapped URL = /context/{id}/items/recommended.
	 * RequestMethod = GET.
	 * @param pageSort Defines the parameters of the response page
	 * @param uriBuilder Used for creating a URI
	 * @param pathContextId Context Id for which Recently Viewed API is called. Can be taken from URL
	 * @param cookieContextId Context Id for which Recently Viewed API is called. Can be taken from cookie
	 * @param productInfo Parameter which can be set to either minimal or intermediate
	 * @param request HTTP request
	 * @return ContextResponse object
	 * @throws AbstractException User defined Exception
	 * @throws AbstractRuntimeException User defined Exception
	 */
	@Timed(name = "Recommended")
	@RequestMapping(value = "/context/{id}/items/recommended", method = RequestMethod.GET)
	public ContextResponse<Item> getRecommendedItemsForContext(final PagingAndSorting pageSort, final UriComponentsBuilder uriBuilder,
			@CookieValue(value = "context_id", defaultValue = "null") final String cookieContextId,
			@PathVariable("id") final String pathContextId,
			@RequestParam(value = "productInfo", defaultValue = ApiConstants.PROD_INFO_MINIMAL) final String productInfo,
			final HttpServletRequest request) throws AbstractException, AbstractRuntimeException {
		String contextId = pathContextId;
		if (this.LOGGER.isInfoEnabled()) {
			this.LOGGER.info("Getting context_id from URL - context_id = " + contextId);
		}
		if (this.COOKIE_PATH_VARIABLE.equals(pathContextId) && cookieContextId != null && !"null".equals(cookieContextId)) {
			contextId = cookieContextId;
		}
		if ((!RequestParamValidator.isValid(contextId))) { throw new InvalidParameterException(InvalidParameterException.INVALID_CONTEXT_ID,
				"Invalid Context Id. Context Id = " + contextId); }

		final Page<Item> page = this.itemService.findRecommendedItemsForContextId(pageSort, contextId, productInfo);

		final ContextResponse<Item> contextResponse = this.responseBuilder.responseBuilderForRecommendedItems(page, contextId, pageSort);

		return contextResponse;
	}

}
