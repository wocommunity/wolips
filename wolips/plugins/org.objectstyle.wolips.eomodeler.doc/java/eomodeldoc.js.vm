visibility = {};

visibility['attribute'] = {}
visibility['attribute']['details'] = false;
visibility['attribute']['nonClassProperty'] = true;
visibility['attribute']['inheritedProperty'] = true;

visibility['relationship'] = {}
visibility['relationship']['details'] = false;
visibility['relationship']['nonClassProperty'] = true;
visibility['relationship']['inheritedProperty'] = true;

visibility['fetchSpec'] = {}
visibility['fetchSpec']['details'] = false;

visibility['entityIndex'] = {}
visibility['entityIndex']['details'] = false;

function toggleProperty(elementType, flagName) {
	visibility[elementType][flagName] = !visibility[elementType][flagName];
	updateProperties(elementType, visibility[elementType]['nonClassProperty'], visibility[elementType]['inheritedProperty'], visibility[elementType]['details']);
}

function initializeDetailsToggleForSelector(selector) {
	$$(selector).each(function(e) {
	  Event.observe(e, "click", function() { e.up('li').down('.details').toggle() });
	});
}

function initializeDetailsToggle() {
	initializeDetailsToggleForSelector('li.attribute div.name');
	initializeDetailsToggleForSelector('li.relationship div.name');
}

function setDetailsVisible(element, detailsVisible) {
		if (detailsVisible) {
			element.show();
		}
		else {
			element.hide();
		}
}
	
function updateProperties(propertyClassName, nonClassVisible, inheritedVisible, detailsVisible) {
	$$('.' + propertyClassName).each(function(e) {
		var propertyVisible = true;
		if (e.hasClassName('nonClassProperty') && !nonClassVisible) {
			propertyVisible = false;
		}
		else if (e.hasClassName('inheritedProperty') && !inheritedVisible) {
			propertyVisible = false;
		}
		if (propertyVisible) {
			e.show();
		}
		else {
			e.hide();
		}
	});

	$$('.' + propertyClassName + ' .details').each(function(e) {
		setDetailsVisible(e, detailsVisible); 
	});

	updateToggleLink($(propertyClassName + '_details_toggle'), detailsVisible, 'details');
	updateToggleLink($(propertyClassName + '_nonClassProperty_toggle'), nonClassVisible, 'non-class');
	updateToggleLink($(propertyClassName + '_inheritedProperty_toggle'), inheritedVisible, 'inherited');
}

function updateToggleLink(toggleLink, visible, displayName) {
	if (toggleLink) {
		if (visible) {
			toggleLink.innerHTML = 'hide ' + displayName;
		}
		else {
			toggleLink.innerHTML = 'show ' + displayName;
		}
	}
}
