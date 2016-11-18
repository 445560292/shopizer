package com.salesmanager.shop.utils;

import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.constants.Constants;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Component
public class LanguageUtils {
	
	@Inject
	LanguageService languageService;
	
	/**
	 * Determines request language based on store rules
	 * @param request
	 * @return
	 */
	public Language getRequestLanguage(HttpServletRequest request, HttpServletResponse response) {
		
		Locale locale = null;
		
		Language language = (Language) request.getSession().getAttribute(Constants.LANGUAGE);
		

		if(language==null) {
			try {
				
					locale = LocaleContextHolder.getLocale();//should be browser locale
				
				
					MerchantStore store = (MerchantStore)request.getSession().getAttribute(Constants.MERCHANT_STORE);
					if(store!=null) {
						language = store.getDefaultLanguage();
						if(language!=null) {
							locale = languageService.toLocale(language);
							if(locale!=null) {
								LocaleContextHolder.setLocale(locale);
							}
							request.getSession().setAttribute(Constants.LANGUAGE, language);
					}
				
					if(language==null) {
						language = languageService.toLanguage(locale);
						request.getSession().setAttribute(Constants.LANGUAGE, language);
					}
				
				}

			} catch(Exception e) {
				if(language==null) {
					try {
						language = languageService.getByCode(Constants.DEFAULT_LANGUAGE);
					} catch(Exception ignore) {}
				}
			}
		} else {
			
			
			Locale localeFromContext = LocaleContextHolder.getLocale();//should be browser locale
			if(!language.getCode().equals(localeFromContext.getLanguage())) {
				//get locale context
				language = languageService.toLanguage(localeFromContext);
			}

		}
		
		if(language != null) {
			locale = languageService.toLocale(language);
		} else {
			language = languageService.toLanguage(locale);
		}
		
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		if(localeResolver!=null) {
			localeResolver.setLocale(request, response, locale);
		}
		response.setLocale(locale);

		return language;
	}

}
