/*
 waitMe - 1.16 [25.07.16]
 Author: vadimsva
 Github: https://github.com/vadimsva/waitMe
 */
(function (b) {
    b.fn.waitMe = function (p) {
        return this.each(function () {
            var f = b(this), x, g, d, u = !1, r = "background-color", t = "", q = "", v, a, w, l = {
                init: function () {
                    function y(a) {
                        m.css({top: "auto", transform: "translateY(" + a + "px) translateZ(0)"})
                    }

                    a = b.extend({
                        effect: "timer",
                        text: "",
                        bg: "rgba(255,255,255,0.7)",
                        color: "#000",
                        maxSize: "",
                        source: "",
                        onClose: function () {
                        }
                    }, p);
                    w = (new Date).getMilliseconds();
                    v = b('<div class="waitMe" data-waitme_id="' + w + '"></div>');
                    switch (a.effect) {
                        case "none":
                            d = 0;
                            break;
                        case "bounce":
                            d = 3;
                            break;
                        case "rotateplane":
                            d =
                                1;
                            break;
                        case "stretch":
                            d = 5;
                            break;
                        case "orbit":
                            d = 2;
                            break;
                        case "roundBounce":
                            d = 12;
                            break;
                        case "win8":
                            d = 5;
                            u = !0;
                            break;
                        case "win8_linear":
                            d = 5;
                            u = !0;
                            break;
                        case "ios":
                            d = 12;
                            break;
                        case "facebook":
                            d = 3;
                            break;
                        case "rotation":
                            d = 1;
                            r = "border-color";
                            break;
                        case "timer":
                            d = 2;
                            var c = b.isArray(a.color) ? a.color[0] : a.color;
                            t = "border-color:" + c;
                            break;
                        case "pulse":
                            d = 1;
                            r = "border-color";
                            break;
                        case "progressBar":
                            d = 1;
                            break;
                        case "bouncePulse":
                            d = 3;
                            break;
                        case "img":
                            d = 1
                    }
                    "" !== t && (t += ";");
                    if (0 < d) {
                        if ("img" === a.effect)q = '<img src="' + a.source +
                            '">'; else for (var e = 1; e <= d; ++e)b.isArray(a.color) ? (c = a.color[e], void 0 == c && (c = "#000")) : c = a.color, q = u ? q + ('<div class="waitMe_progress_elem' + e + '"><div style="' + r + ":" + c + '"></div></div>') : q + ('<div class="waitMe_progress_elem' + e + '" style="' + r + ":" + c + '"></div>');
                        g = b('<div class="waitMe_progress ' + a.effect + '" style="' + t + '">' + q + "</div>")
                    }
                    a.text && "" === a.maxSize && (c = b.isArray(a.color) ? a.color[0] : a.color, x = b('<div class="waitMe_text" style="color:' + c + '">' + a.text + "</div>"));
                    var k = f.find("> .waitMe");
                    k && k.remove();
                    c = b('<div class="waitMe_content"></div>');
                    c.append(g, x);
                    v.append(c);
                    "HTML" == f[0].tagName && (f = b("body"));
                    f.addClass("waitMe_container").attr("data-waitme_id", w).append(v);
                    var k = f.find("> .waitMe"), m = f.find(".waitMe_content");
                    k.css({background: a.bg});
                    "" !== a.maxSize && (e = g.outerHeight(), g.outerWidth(), "img" === a.effect ? (g.css({height: a.maxSize + "px"}), g.find(">img").css({maxHeight: "100%"}), m.css({marginTop: -m.outerHeight() / 2 + "px"})) : a.maxSize < e && ("stretch" == a.effect ? (g.css({height: a.maxSize + "px"}), g.find("> div").css({margin: "0 5%"})) :
                        c.css({zoom: a.maxSize / e - .2})));
                    m.css({marginTop: -m.outerHeight() / 2 + "px"});
                    if (f.outerHeight() > b(window).height()) {
                        var c = b(window).scrollTop(), h = m.outerHeight(), n = f.offset().top, l = f.outerHeight(), e = c - n + b(window).height() / 2;
                        0 > e && (e = Math.abs(e));
                        0 <= e - h && e + h <= l ? n - c > b(window).height() / 2 && (e = h) : e = c > n + l - h ? c - n - h : c - n + h;
                        y(e);
                        b(document).scroll(function () {
                            var a = b(window).scrollTop() - n + b(window).height() / 2;
                            0 <= a - h && a + h <= l && y(a)
                        })
                    }
                    k.on("destroyed", function () {
                        if (a.onClose && b.isFunction(a.onClose))a.onClose();
                        k.trigger("close")
                    });
                    b.event.special.destroyed = {
                        remove: function (a) {
                            a.handler && a.handler()
                        }
                    };
                    return k
                }, hide: function () {
                    var a = f.attr("data-waitme_id");
                    f.removeClass("waitMe_container").removeAttr("data-waitme_id");
                    f.find('.waitMe[data-waitme_id="' + a + '"]').remove()
                }
            };
            if (l[p])return l[p].apply(this, Array.prototype.slice.call(arguments, 1));
            if ("object" === typeof p || !p)return l.init.apply(this, arguments)
        })
    };
    b(window).on("load", function () {
        b("body.waitMe_body").addClass("hideMe");
        setTimeout(function () {
            b("body.waitMe_body").find(".waitMe_container:not([data-waitme_id])").remove();
            b("body.waitMe_body").removeClass("waitMe_body hideMe")
        }, 200)
    })
})(jQuery);