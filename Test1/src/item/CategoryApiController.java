/**
 * CategoryApiController.java        Nov 21, 2012
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

/**
 * Class used to fetch API response based on Category ID.
 * API covered is <ol><li>Most Popular API</li></ol>
 * @author atindriya.anik.ghosh
 *
 */
@Controller
public class CategoryApiController extends AbstractApiController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CategoryApiController.class);
	
	@Autowired
	private ItemService itemService;
	
	@Autowired
	private ResponseBuilder responseBuilder;

	/**
	 * Checks if categoryId is valid and calls service layer method to get a {@link Page} object which contains a list of items. 
	 * Calls {@link ResponseBuilder#responseBuilderForMostPopular(Page, String, PagingAndSorting)} method to build the API response.
	 * @param pageSort Defines the parameters of the response page
	 * @param categoryId Category Id for which Most Popular API is called
	 * @param productInfo Can be set to either intermediate or minimal
	 * @return ContextResponse object
	 * @throws AbstractException User defined Exception
	 * @throws AbstractRuntimeException User defined Exception
	 */
	private ContextResponse<Item> getMostPopularItems(final PagingAndSorting pageSort, final String categoryId, final String productInfo)
			throws AbstractException,AbstractRuntimeException {

		if ((!RequestParamValidator.isValid(categoryId))) { throw new InvalidParameterException(InvalidParameterException.INVALID_CONTEXT_ID,
				"Invalid Category Id. Category Id = " + categoryId);
		}
		final Page<Item> page = this.itemService.findMostPopularItemsForCategory(pageSort, categoryId, productInfo);

		final ContextResponse<Item> contextResponse = this.responseBuilder.responseBuilderForMostPopular(page, categoryId, pageSort);

		return contextResponse;
	}

	/**
	 * This method accepts the request from the user for Most Popular API and returns a {@link ContextResponse} object as response. The response is in the form of a JSON/XML file.
	 * Mapped URL = /categories/items/popular.
	 * RequestMethod = GET.
	 * @param pageSort Defines the parameters of the response page
	 * @param uriBuilder Used for creating a URI
	 * @param productInfo Parameter which can be set to either minimal or intermediate
	 * @param request HTTP request
	  * @return ContextResponse object
	 * @throws AbstractException User defined Exception
	 * * @throws AbstractRuntimeException User defined Exception
	 */
	@Timed(name = "Most Popular - All Categories")
	@RequestMapping(value = "/categories/items/popular", method = RequestMethod.GET)
	public ContextResponse<Item> getMostPopularItemsForAllCategories(final PagingAndSorting pageSort, final UriComponentsBuilder uriBuilder,
			@RequestParam(value = ApiConstants.PROD_INFO_PARAM, defaultValue = ApiConstants.PROD_INFO_MINIMAL) final String productInfo,
			final HttpServletRequest request) throws AbstractException,AbstractRuntimeException{
		if (this.LOGGER.isInfoEnabled()) {
			this.LOGGER.info("Category Id = " + ApiConstants.CATEGORY_ID_FOR_ALL);
		}
		return this.getMostPopularItems(pageSort, ApiConstants.CATEGORY_ID_FOR_ALL, productInfo);
	}

	/**
	 * This method accepts the request from the user for Most Popular API and returns a {@link ContextResponse} object as response. The response is in the form of a JSON/XML file.
	 * Mapped URL = /category/{categoryId}/items/popular.
	 * RequestMethod = GET.
	 * @param pageSort Defines the parameters of the response page
	 * @param uriBuilder Used for creating a URI
	 * @param categoryId Category Id for which Most Popular API is called. Can be taken from URL or from cookie
	 * @param productInfo Parameter which can be set to either minimal or intermediate
	 * @param request HTTP request
	 * @return {@link ContextResponse} object
	 * @throws {@link AbstractException}
	 */
	@RequestMapping(value = "/category/{categoryId}/items/popular", method = RequestMethod.GET)
	@Timed(name = "Most Popular - For a Category")
	public ContextResponse<Item> getMostPopularItemsForCategory(final PagingAndSorting pageSort, final UriComponentsBuilder uriBuilder,
			@PathVariable("categoryId") final String categoryId, @RequestParam(
					value = ApiConstants.PROD_INFO_PARAM, defaultValue = ApiConstants.PROD_INFO_MINIMAL) final String productInfo,
			final HttpServletRequest request) throws AbstractException {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Category Id = " + categoryId);
		}
		return getMostPopularItems(pageSort, categoryId, productInfo);
	}

}
