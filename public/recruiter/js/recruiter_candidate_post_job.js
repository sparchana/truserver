/**
 * Created by hawk on 15/10/16.
 */
$(document).ready(function(){
    $('select').material_select();
});
$(document).scroll(function(){
    if ($(this).scrollTop() > 80) {
        $('nav').css({"background": "rgba(0, 0, 0, 0.8)"});
    }
    else{
        $('nav').css({"background": "transparent"});
    }
});
function toJobDetail(){
    $('ul.tabs').tabs('select_tab', 'jobDetails');
    $('body').scrollTop(0);
}
function toJobRequirement(){
    $('ul.tabs').tabs('select_tab', 'jobRequirement');
    $('body').scrollTop(0);
}