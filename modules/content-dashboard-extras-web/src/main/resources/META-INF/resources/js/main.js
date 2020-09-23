(function() {
	
var readFavorites = function(onSuccess, onFailure) {
	var action = document.location.pathname + '?p_p_id=com_liferay_content_dashboard_web_portlet_ContentDashboardAdminPortlet&p_p_lifecycle=2&p_p_state=maximized&p_p_mode=view&' + 
	'p_p_resource_id=%2Fcontent_dashboard_extras%2Fget_favorites';
	
	var xhr = new XMLHttpRequest();
	xhr.open("GET", action, true);
	xhr.onreadystatechange = function() { 
	    if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
	    	if(onSuccess) {
	    		onSuccess(JSON.parse(this.response));
	    	} 
	    } else if(onFailure) {
    		onFailure(this);
    	}
	}
	
	xhr.send('');
};

var saveFavorites = function(favorites, onSuccess, onFailure) {
	var action = document.location.pathname + '?p_p_id=com_liferay_content_dashboard_web_portlet_ContentDashboardAdminPortlet&p_p_lifecycle=2&p_p_state=maximized&p_p_mode=view&' +
	'p_p_resource_id=%2Fcontent_dashboard_extras%2Fedit_favorites';	
	
	var xhr = new XMLHttpRequest();
	xhr.open("POST", action, true);
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	xhr.onreadystatechange = function() { 
	    if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
	    	if(onSuccess) {
	    		onSuccess(this);
	    	} 
	    } else if(onFailure) {
    		onFailure(this);
    	}
	}
	
	xhr.send("_com_liferay_content_dashboard_web_portlet_ContentDashboardAdminPortlet_favorites=" + encodeURIComponent(JSON.stringify(favorites)));
};

var addFavoriteMenuEntry = function(fav, ul) {
	var link = document.createElement('a');
	link.href = document.location.pathname + '?' + fav.queryString.replace('{authToken}', Liferay.authToken);
	link.textContent = fav.name;
	link.className = 'dropdown-item';
	var li = document.createElement('li');
	li.appendChild(link);
	
	var deleteLink = document.createElement('a');
	deleteLink.href = '';
	deleteLink.innerHTML = '<svg class="lexicon-icon lexicon-icon-times" focusable="false" role="presentation" viewBox="0 0 512 512"><path class="lexicon-icon-outline" d="M301.1,256.1L502.3,54.9c30.1-30.1-16.8-73.6-45.2-45.2L255.9,210.8L54.6,9.7C24.6-20.4-19,26.5,9.4,54.9l201.2,201.2L9.3,457.3c-28.9,28.9,15.8,74.6,45.2,45.2l201.3-201.2l201.3,201.2c28.9,28.9,74.2-16.3,45.2-45.2L301.1,256.1z"></path></svg>';
	deleteLink.className = 'dropdown-item delete';
	deleteLink.addEventListener('click', function(e) {
		
		e.preventDefault();
		e.stopPropagation();
		
		li.remove(); 

		readFavorites(function(favorites) {
			
			var newFavorites = favorites.filter(f => f.id !== fav.id);
			
			saveFavorites(newFavorites);
		});
	});
	li.appendChild(deleteLink);

	if(!ul) { 
		ul = document.querySelector('.extras-favorites-dropdown-menu ul');
	}
	
	ul.appendChild(li);
};

var createFavoritesMenu = function(navItem, favorites) {
	
	var menu = document.createElement('div');

	menu.className = 'extras-favorites-dropdown-menu dropdown-menu';

	var ul = document.createElement('ul');
	ul.className = 'list-unstyled';
	menu.appendChild(ul);
	
	ul.innerHTML = '<li class="dropdown-subheader" role="presentation">Favorite Queries</li>';
	
	for(i=0; i<favorites.length; i++) {
		var fav = favorites[i];
		
		addFavoriteMenuEntry(fav, ul);
	}
	
	navItem.appendChild(menu);
	
	return menu;
};

var addFavoritesNavItem = function(favorites) {
	
	var navItem = document.createElement('li');
	navItem.className = 'nav-item extras-favorites-nav-item';
	var link = document.createElement('a');
	navItem.appendChild(link);
	link.href = '';
	link.innerHTML = '⭐';
	
	var someCaret = document.querySelector('.lexicon-icon-caret-bottom');
	var caret = someCaret.cloneNode(true);
	caret.innerHTML = someCaret.innerHTML;
	
	link.appendChild(caret);
	
	var filterNavItem = document.querySelector('#contentManagementToolbar .dropdown.nav-item');
	
	filterNavItem.parentElement.insertBefore(navItem, filterNavItem);
	
	var menu = createFavoritesMenu(navItem, favorites);
	
	link.addEventListener('click', function(e) {
		e.preventDefault();
		e.stopPropagation();
		
		if(menu.classList.contains('show')) {
			menu.classList.remove('show');
			return;
		}
		
		menu.classList.add('show')
		
		var body = document.querySelector('body');
		
		var bodyClickListener = function(e) {
			
			if(!menu.contains(e.target)) {
				menu.classList.remove('show');
				body.removeEventListener("click", bodyClickListener);
			}
		};
		
		body.addEventListener('click', bodyClickListener);
	});
};

var uuidv4 = function() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

var completeToolbar = function(favorites) {
	
	addFavoritesNavItem(favorites);
	
	var clearLink = document.querySelector('#contentManagementToolbar .tbar-section a');

	if(clearLink != null) {
		
		addToFavoritesURL = '';
		addToFavoriteLabel = 'Add to Favorites'
		
		var addToFavoritesLink = document.createElement('a');
		addToFavoritesLink.className = 'component-link tbar-link';
		addToFavoritesLink.style = 'margin-left: 20px';
		addToFavoritesLink.href = addToFavoritesURL;
		addToFavoritesLink.textContent = addToFavoriteLabel;
		
		addToFavoritesLink.addEventListener("click", function(e) {
			
			e.preventDefault();
			
			e.stopPropagation();
			
			var promptDefaultValue = '';
			
			var criteria = document.querySelectorAll('#contentManagementToolbar .tbar-nav .label-item.label-item-expand');
			
			for (var i = 0; i < criteria.length; i++) {
				var criterion = criteria[i];
				console.log(criterion);
				promptDefaultValue += criterion.innerText + ((i != criteria.length - 1)?', ':'');
			}
			
			var name = prompt('Enter favorite name', promptDefaultValue);
			
			if(name == null || name.trim().length == 0) {
				return;
			}
			
			var id = uuidv4();
			
			var queryString = document.location.search.substring(1,document.location.search.length);
			
			queryString = queryString.replace('p_p_auth=' + Liferay.authToken + '&','p_p_auth={authToken}&');
			
			var favorite = {
				id: id,
				name: name,
				queryString: queryString
			}
			
			favorites.push(favorite);
			
			saveFavorites(favorites);
			
			addFavoriteMenuEntry(favorite);
		});	
		
		clearLink.parentElement.appendChild(addToFavoritesLink);
	}
}

var configureExportMenu = function(){
	
	var toolbar = document.querySelector('.extras-export-toolbar');
	
	if(!toolbar) {
		return;
	}
	
	var menu = toolbar.querySelector('.dropdown-menu');
	
	var link = toolbar.querySelector('.nav-item a');
	
	link.addEventListener('click', function(e) {
		
		e.preventDefault();
		e.stopPropagation();
		
		if(menu.classList.contains('show')) {
			menu.classList.remove('show');
			return;
		}
		
		menu.classList.add('show');
		
		var body = document.querySelector('body');
		
		var bodyClickListener = function(e) {
			
			if(!menu.contains(e.target)) {
				menu.classList.remove('show');
				body.removeEventListener("click", bodyClickListener);
			}
		};
		
		body.addEventListener('click', bodyClickListener);
	});
};

var initializationInProgress = false;

var init = function() {
	
	var initialized = document.querySelector('.extras-favorites-nav-item') !== null; 
	
	if(!initialized && !initializationInProgress) {

		initializationInProgress = true;
		
		readFavorites(function(favorites) {
			
			completeToolbar(favorites);
			
			initializationInProgress = false;
		});
		
		configureExportMenu();
	}
}

var observe = function() {
	const targetNode = document.getElementById('contentManagementToolbar');
	const config = { attributes: true, childList: false, subtree: true };
	const callback = function(mutationsList, observer) {
		
		if(mutationsList.some(m =>  m.target.className === 'navbar-nav' ||  m.attributeName === 'data-children-count')) {
			init();
		}
	};
	const observer = new MutationObserver(callback);
	observer.observe(targetNode, config);
}

observe();

})();
