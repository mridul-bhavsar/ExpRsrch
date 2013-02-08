/**
 * ContextApiController.java		 Nov 14, 2012
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

/**
 * Class used to fetch API response based on Item ID.
 * API covered is <ol><li>Customer Also Viewed API</li></ol>
 * @author mridul.bhavsar
 */
@Controller
public class ItemApiController extends AbstractApiController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ItemApiController.class);

	@Autowired
	private ItemService itemService;

	@Autowired
	private ResponseBuilder responseBuilder;

	/**
	 * This method accepts the request from the user for Customer Also Viewed API and returns a {@link ContextResponse} object as response.
	 * The response is in the form of a JSON/XML file.
	 * Mapped URL = /item/{id}/alsoviewed.
	 * RequestMethod = GET.
	 * @param pageSort Defines the parameters of the response page
	 * @param uriBuilder Used for creating a URI
	 * @param itemId Item Id for which Customer Also Viewed API is called. Can be taken from URL or from cookie
	 * @param prodInfo Parameter which can be set to either minimal or intermediate
	 * @param request HTTP request
	 * @return ContextResponse object
	 * @throws AbstractException User defined Exception
	 * @throws AbstractRuntimeException User defined Exception
	 */
	@Timed(name = "Also Viewed")
	@RequestMapping(value = "/item/{id}/alsoviewed", method = RequestMethod.GET)
	// , produces = "application/json"/)
	public ContextResponse<Item> getAlsoViewedtItemsForItem(final PagingAndSorting pageSort, final UriComponentsBuilder uriBuilder, 
			@PathVariable("id") final String itemId, @RequestParam(value = "productInfo", defaultValue = "minimal") final String prodInfo, 
			final HttpServletRequest request) throws AbstractException, AbstractRuntimeException {

		if (this.LOGGER.isInfoEnabled()) {
			this.LOGGER.info("Getting item_id from URL - itemId = " + itemId);

		}
		if ((!RequestParamValidator.isValid(itemId))) { throw new InvalidParameterException(InvalidParameterException.INVALID_CONTEXT_ID,
				"Invalid Item Id. Item Id = " + itemId);
		}
		final Page<Item> page = this.itemService.findAlsoViewedItemsForItem(pageSort, itemId, prodInfo, ApiConstants.ALSO_VIEWED_API);

		final ContextResponse<Item> contextResponse = this.responseBuilder.responseBuilderForItem(page, itemId, pageSort, ApiConstants.ALSO_VIEWED_API);

		return contextResponse;
	}
	
	/**
	 * This method accepts the request from the user for Customer Also Bought API and returns a {@link ContextResponse} object as response.
	 * The response is in the form of a JSON/XML file.
	 * Mapped URL = /item/{id}/alsobought.
	 * RequestMethod = GET.
	 * @param pageSort Defines the parameters of the response page
	 * @param uriBuilder Used for creating a URI
	 * @param itemId Item Id for which Customer Also Bought API is called. Can be taken from URL or from cookie
	 * @param prodInfo Parameter which can be set to either minimal or intermediate
	 * @param request HTTP request
	 * @return ContextResponse object
	 * @throws AbstractException User defined Exception
	 * @throws AbstractRuntimeException User defined Exception
	 */
	@Timed(name = "Also Bought")
	@RequestMapping(value = "/item/{id}/alsobought", method = RequestMethod.GET)
	// , produces = "application/json"/)
	public ContextResponse<Item> getAlsoBoughtItemsForItem(final PagingAndSorting pageSort, final UriComponentsBuilder uriBuilder, 
			@PathVariable("id") final String itemId, @RequestParam(value = "productInfo", defaultValue = "minimal") final String prodInfo, 
			final HttpServletRequest request) throws AbstractException, AbstractRuntimeException {
		if (this.LOGGER.isInfoEnabled()) {
			this.LOGGER.info("Getting item_id from URL - itemId = " + itemId);
		}

		if ((!RequestParamValidator.isValid(itemId))) { throw new InvalidParameterException(InvalidParameterException.INVALID_CONTEXT_ID,

				"Invalid Item Id. Item Id = " + itemId);
		}
		final Page<Item> page = this.itemService.findAlsoBoughtItemsForItem(pageSort, itemId, prodInfo, ApiConstants.ALSO_BOUGHT_API);

		final ContextResponse<Item> contextResponse = this.responseBuilder.responseBuilderForItem(page, itemId, pageSort, ApiConstants.ALSO_BOUGHT_API);
				
		return contextResponse;
	}

}
