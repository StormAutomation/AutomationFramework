$(document).ready(function() {
    // customized
    $('.top-banner-root').prepend('<img style="height:45px; float:left; padding-left:5px;" src="http://stormautomation.com/wp-content/uploads/2018/01/STORM-Logo-Wide.png"/>');
    let rawNavigate = document.createElement('li');
    rawNavigate.innerHTML='<a href="#" class="navigator-link " panel-name="raw-test-output">'+
                    '<span>Terminal output</span>'+
                  '</a> <!-- navigator-link  -->'+
                '<link href="raw.txt" type="text/plain"/>'
    
    $('.suite-section-content')[0].getElementsByTagName('ul')[0].append(rawNavigate);

rawNavigate.style.height = window.innerHeight
    $('.main-panel-root').append('<div panel-name="raw-test-output" class="panel" style="display: none;">'+
          '<div class="main-panel-header rounded-window-top">'+
            '<span class="header-content">Raw Test Output</span>'+
          '</div> <!-- main-panel-header rounded-window-top -->'+
          '<div class="main-panel-content rounded-window-bottom">'+
            '<div class="raw-text">'+
              '<object type="text/plain" data="raw.txt" style="width: 100%; height: '+(window.innerHeight-125)+'px;"></object>'+
            '</div>'+
          '</div> <!-- main-panel-content rounded-window-bottom -->'+
        '</div> <!-- panel -->'+
      '</div>');
    // end of customized

    $('a.navigator-link').click(function() {
        // Extract the panel for this link
        var panel = getPanelName($(this));

        // Mark this link as currently selected
        $('.navigator-link').parent().removeClass('navigator-selected');
        $(this).parent().addClass('navigator-selected');

        showPanel(panel);
    });

    installMethodHandlers('failed');
    installMethodHandlers('skipped');
    installMethodHandlers('passed', true); // hide passed methods by default

    $('a.method').click(function() {
        showMethod($(this));
        return false;
    });

    // Hide all the panels and display the first one (do this last
    // to make sure the click() will invoke the listeners)
    $('.panel').hide();
    $('.navigator-link').first().click();

    // Collapse/expand the suites
    $('a.collapse-all-link').click(function() {
        var contents = $('.navigator-suite-content');
        if (contents.css('display') == 'none') {
            contents.show();
            $('.navigator-root').css('width','400px');
            $('.suite-name').show();
        } else {
            contents.hide();
            $('.navigator-root').css('width','25px');
            $('.suite-name').hide();
        }
    });

    // Keep the navigator div always visible
    var $scrollingDiv = $(".navigator-root");
    $(window).scroll(function() {
        $scrollingDiv.css('height', $(window).height() - 65);
        $scrollingDiv.stop()
            .animate({"marginTop": ($(window).scrollTop() + 60) + "px"} );
    });
});

// The handlers that take care of showing/hiding the methods
function installMethodHandlers(name, hide) {
    function getContent(t) {
    return $('.method-list-content.' + name + "." + t.attr('panel-name'));
    }

    function getHideLink(t, name) {
        var s = 'a.hide-methods.' + name + "." + t.attr('panel-name');
        return $(s);
    }

    function getShowLink(t, name) {
        return $('a.show-methods.' + name + "." + t.attr('panel-name'));
    }

    function getMethodPanelClassSel(element, name) {
        var panelName = getPanelName(element);
    var sel = '.' + panelName + "-class-" + name;
        return $(sel);
    }

    $('a.hide-methods.' + name).click(function() {
        var w = getContent($(this));
        w.hide();
        getHideLink($(this), name).hide();
        getShowLink($(this), name).show();
    getMethodPanelClassSel($(this), name).hide();
    });

    $('a.show-methods.' + name).click(function() {
        var w = getContent($(this));
        w.show();
        getHideLink($(this), name).show();
        getShowLink($(this), name).hide();
    showPanel(getPanelName($(this)));
    getMethodPanelClassSel($(this), name).show();
    });

    if (hide) {
        $('a.hide-methods.' + name).click();
    } else {
        $('a.show-methods.' + name).click();
    }
}

function getHashForMethod(element) {
    return element.attr('hash-for-method');
}

function getPanelName(element) {
    return element.attr('panel-name');
}

function showPanel(panelName) {
    $('.panel').hide();
    var panel = $('.panel[panel-name="' + panelName + '"]');
    panel.show();
}

function showMethod(element) {
    var hashTag = getHashForMethod(element);
    var panelName = getPanelName(element);
    showPanel(panelName);
    var current = document.location.href;
    var base = current.substring(0, current.indexOf('#'))
    document.location.href = base + '#' + hashTag;
    var newPosition = $(document).scrollTop() - 65;
    $(document).scrollTop(newPosition);
}

function drawTable() {
    for (var i = 0; i < suiteTableInitFunctions.length; i++) {
        window[suiteTableInitFunctions[i]]();
    }

    for (var k in window.suiteTableData) {
        var v = window.suiteTableData[k];
        var div = v.tableDiv;
        var data = v.tableData
        var table = new google.visualization.Table(document.getElementById(div));
        table.draw(data, {
            showRowNumber : false
        });
    }
}