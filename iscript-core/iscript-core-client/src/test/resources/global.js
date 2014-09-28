//http://image.yihaodianimg.com/front-homepage/global/js/global_index_top.js?1020828
ilog('location.href:'+location.href);
 /* SVN.committedRevision=1030838 */
var requirejs, require, define;
(function(global) {
    var req, s, head, baseElement, dataMain, src, interactiveScript, currentlyAddingScript, mainScript, subPath, version = "2.1.11", commentRegExp = /(\/\*([\s\S]*?)\*\/|([^:]|^)\/\/(.*)$)/mg, cjsRequireRegExp = /[^.]\s*require\s*\(\s*["']([^'"\s]+)["']\s*\)/g, jsSuffixRegExp = /\.js$/, currDirRegExp = /^\.\//, op = Object.prototype, ostring = op.toString, hasOwn = op.hasOwnProperty, ap = Array.prototype, apsp = ap.splice, isBrowser = !!(typeof window !== "undefined" && typeof navigator !== "undefined" && window.document), isWebWorker = !isBrowser && typeof importScripts !== "undefined", readyRegExp = isBrowser && navigator.platform === "PLAYSTATION 3" ? /^complete$/ : /^(complete|loaded)$/, defContextName = "_", isOpera = typeof opera !== "undefined" && opera.toString() === "[object Opera]", contexts = {}, cfg = {}, globalDefQueue = [], useInteractive = false;
    function isFunction(it) {
        return ostring.call(it) === "[object Function]"
    }
    function isArray(it) {
        return ostring.call(it) === "[object Array]"
    }
    function each(ary, func) {
        if (ary) {
            var i;
            for (i = 0; i < ary.length; i += 1) {
                if (ary[i] && func(ary[i], i, ary)) {
                    break
                }
            }
        }
    }
    function eachReverse(ary, func) {
        if (ary) {
            var i;
            for (i = ary.length - 1; i > -1; i -= 1) {
                if (ary[i] && func(ary[i], i, ary)) {
                    break
                }
            }
        }
    }
    function hasProp(obj, prop) {
        return hasOwn.call(obj, prop)
    }
    function getOwn(obj, prop) {
        return hasProp(obj, prop) && obj[prop]
    }
    function eachProp(obj, func) {
        var prop;
        for (prop in obj) {
            if (hasProp(obj, prop)) {
                if (func(obj[prop], prop)) {
                    break
                }
            }
        }
    }
    function mixin(target, source, force, deepStringMixin) {
        if (source) {
            eachProp(source, function(value, prop) {
                if (force || !hasProp(target, prop)) {
                    if (deepStringMixin && typeof value === "object" && value && !isArray(value) && !isFunction(value) && !(value instanceof RegExp)) {
                        if (!target[prop]) {
                            target[prop] = {}
                        }
                        mixin(target[prop], value, force, deepStringMixin)
                    } else {
                        target[prop] = value
                    }
                }
            })
        }
        return target
    }
    function bind(obj, fn) {
        return function() {
            return fn.apply(obj, arguments)
        }
    }
    function scripts() {
        return document.getElementsByTagName("script")
    }
    function defaultOnError(err) {
        throw err
    }
    function getGlobal(value) {
        if (!value) {
            return value
        }
        var g = global;
        each(value.split("."), function(part) {
            g = g[part]
        });
        return g
    }
    function makeError(id, msg, err, requireModules) {
        var e = new Error(msg + "\nhttp://requirejs.org/docs/errors.html#" + id);
        e.requireType = id;
        e.requireModules = requireModules;
        if (err) {
            e.originalError = err
        }
        return e
    }
    if (typeof define !== "undefined") {
        return
    }
    if (typeof requirejs !== "undefined") {
        if (isFunction(requirejs)) {
            return
        }
        cfg = requirejs;
        requirejs = undefined
    }
    if (typeof require !== "undefined" && !isFunction(require)) {
        cfg = require;
        require = undefined
    }
    function newContext(contextName) {
        var inCheckLoaded, Module, context, handlers, checkLoadedTimeoutId, config = {waitSeconds: 7,baseUrl: "./",paths: {},bundles: {},pkgs: {},shim: {},config: {}}, registry = {}, enabledRegistry = {}, undefEvents = {}, defQueue = [], defined = {}, urlFetched = {}, bundlesMap = {}, requireCounter = 1, unnormalizedCounter = 1;
        function trimDots(ary) {
            var i, part, length = ary.length;
            for (i = 0; i < length; i++) {
                part = ary[i];
                if (part === ".") {
                    ary.splice(i, 1);
                    i -= 1
                } else {
                    if (part === "..") {
                        if (i === 1 && (ary[2] === ".." || ary[0] === "..")) {
                            break
                        } else {
                            if (i > 0) {
                                ary.splice(i - 1, 2);
                                i -= 2
                            }
                        }
                    }
                }
            }
        }
        function normalize(name, baseName, applyMap) {
            var pkgMain, mapValue, nameParts, i, j, nameSegment, lastIndex, foundMap, foundI, foundStarMap, starI, baseParts = baseName && baseName.split("/"), normalizedBaseParts = baseParts, map = config.map, starMap = map && map["*"];
            if (name && name.charAt(0) === ".") {
                if (baseName) {
                    normalizedBaseParts = baseParts.slice(0, baseParts.length - 1);
                    name = name.split("/");
                    lastIndex = name.length - 1;
                    if (config.nodeIdCompat && jsSuffixRegExp.test(name[lastIndex])) {
                        name[lastIndex] = name[lastIndex].replace(jsSuffixRegExp, "")
                    }
                    name = normalizedBaseParts.concat(name);
                    trimDots(name);
                    name = name.join("/")
                } else {
                    if (name.indexOf("./") === 0) {
                        name = name.substring(2)
                    }
                }
            }
            if (applyMap && map && (baseParts || starMap)) {
                nameParts = name.split("/");
                outerLoop: for (i = nameParts.length; i > 0; i -= 1) {
                    nameSegment = nameParts.slice(0, i).join("/");
                    if (baseParts) {
                        for (j = baseParts.length; j > 0; j -= 1) {
                            mapValue = getOwn(map, baseParts.slice(0, j).join("/"));
                            if (mapValue) {
                                mapValue = getOwn(mapValue, nameSegment);
                                if (mapValue) {
                                    foundMap = mapValue;
                                    foundI = i;
                                    break outerLoop
                                }
                            }
                        }
                    }
                    if (!foundStarMap && starMap && getOwn(starMap, nameSegment)) {
                        foundStarMap = getOwn(starMap, nameSegment);
                        starI = i
                    }
                }
                if (!foundMap && foundStarMap) {
                    foundMap = foundStarMap;
                    foundI = starI
                }
                if (foundMap) {
                    nameParts.splice(0, foundI, foundMap);
                    name = nameParts.join("/")
                }
            }
            pkgMain = getOwn(config.pkgs, name);
            return pkgMain ? pkgMain : name
        }
        function removeScript(name) {
            if (isBrowser) {
                each(scripts(), function(scriptNode) {
                    if (scriptNode.getAttribute("data-requiremodule") === name && scriptNode.getAttribute("data-requirecontext") === context.contextName) {
                        scriptNode.parentNode.removeChild(scriptNode);
                        return true
                    }
                })
            }
        }
        function hasPathFallback(id) {
            var pathConfig = getOwn(config.paths, id);
            if (pathConfig && isArray(pathConfig) && pathConfig.length > 1) {
                pathConfig.shift();
                context.require.undef(id);
                context.require([id]);
                return true
            }
        }
        function splitPrefix(name) {
            var prefix, index = name ? name.indexOf("!") : -1;
            if (index > -1) {
                prefix = name.substring(0, index);
                name = name.substring(index + 1, name.length)
            }
            return [prefix, name]
        }
        function makeModuleMap(name, parentModuleMap, isNormalized, applyMap) {
            var url, pluginModule, suffix, nameParts, prefix = null, parentName = parentModuleMap ? parentModuleMap.name : null, originalName = name, isDefine = true, normalizedName = "";
            if (!name) {
                isDefine = false;
                name = "_@r" + (requireCounter += 1)
            }
            nameParts = splitPrefix(name);
            prefix = nameParts[0];
            name = nameParts[1];
            if (prefix) {
                prefix = normalize(prefix, parentName, applyMap);
                pluginModule = getOwn(defined, prefix)
            }
            if (name) {
                if (prefix) {
                    if (pluginModule && pluginModule.normalize) {
                        normalizedName = pluginModule.normalize(name, function(name) {
                            return normalize(name, parentName, applyMap)
                        })
                    } else {
                        normalizedName = normalize(name, parentName, applyMap)
                    }
                } else {
                    normalizedName = normalize(name, parentName, applyMap);
                    nameParts = splitPrefix(normalizedName);
                    prefix = nameParts[0];
                    normalizedName = nameParts[1];
                    isNormalized = true;
                    url = context.nameToUrl(normalizedName)
                }
            }
            suffix = prefix && !pluginModule && !isNormalized ? "_unnormalized" + (unnormalizedCounter += 1) : "";
            return {prefix: prefix,name: normalizedName,parentMap: parentModuleMap,unnormalized: !!suffix,url: url,originalName: originalName,isDefine: isDefine,id: (prefix ? prefix + "!" + normalizedName : normalizedName) + suffix}
        }
        function getModule(depMap) {
            var id = depMap.id, mod = getOwn(registry, id);
            if (!mod) {
                mod = registry[id] = new context.Module(depMap)
            }
            return mod
        }
        function on(depMap, name, fn) {
            var id = depMap.id, mod = getOwn(registry, id);
            if (hasProp(defined, id) && (!mod || mod.defineEmitComplete)) {
                if (name === "defined") {
                    fn(defined[id])
                }
            } else {
                mod = getModule(depMap);
                if (mod.error && name === "error") {
                    fn(mod.error)
                } else {
                    mod.on(name, fn)
                }
            }
        }
        function onError(err, errback) {
            var ids = err.requireModules, notified = false;
            if (errback) {
                errback(err)
            } else {
                each(ids, function(id) {
                    var mod = getOwn(registry, id);
                    if (mod) {
                        mod.error = err;
                        if (mod.events.error) {
                            notified = true;
                            mod.emit("error", err)
                        }
                    }
                });
                if (!notified) {
                    req.onError(err)
                }
            }
        }
        function takeGlobalQueue() {
            if (globalDefQueue.length) {
                apsp.apply(defQueue, [defQueue.length, 0].concat(globalDefQueue));
                globalDefQueue = []
            }
        }
        handlers = {require: function(mod) {
                if (mod.require) {
                    return mod.require
                } else {
                    return (mod.require = context.makeRequire(mod.map))
                }
            },exports: function(mod) {
                mod.usingExports = true;
                if (mod.map.isDefine) {
                    if (mod.exports) {
                        return (defined[mod.map.id] = mod.exports)
                    } else {
                        return (mod.exports = defined[mod.map.id] = {})
                    }
                }
            },module: function(mod) {
                if (mod.module) {
                    return mod.module
                } else {
                    return (mod.module = {id: mod.map.id,uri: mod.map.url,config: function() {
                            return getOwn(config.config, mod.map.id) || {}
                        },exports: mod.exports || (mod.exports = {})})
                }
            }};
        function cleanRegistry(id) {
            delete registry[id];
            delete enabledRegistry[id]
        }
        function breakCycle(mod, traced, processed) {
            var id = mod.map.id;
            if (mod.error) {
                mod.emit("error", mod.error)
            } else {
                traced[id] = true;
                each(mod.depMaps, function(depMap, i) {
                    var depId = depMap.id, dep = getOwn(registry, depId);
                    if (dep && !mod.depMatched[i] && !processed[depId]) {
                        if (getOwn(traced, depId)) {
                            mod.defineDep(i, defined[depId]);
                            mod.check()
                        } else {
                            breakCycle(dep, traced, processed)
                        }
                    }
                });
                processed[id] = true
            }
        }
        function checkLoaded() {
            var err, usingPathFallback, waitInterval = config.waitSeconds * 1000, expired = waitInterval && (context.startTime + waitInterval) < new Date().getTime(), noLoads = [], reqCalls = [], stillLoading = false, needCycleCheck = true;
            if (inCheckLoaded) {
                return
            }
            inCheckLoaded = true;
            eachProp(enabledRegistry, function(mod) {
                var map = mod.map, modId = map.id;
                if (!mod.enabled) {
                    return
                }
                if (!map.isDefine) {
                    reqCalls.push(mod)
                }
                if (!mod.error) {
                    if (!mod.inited && expired) {
                        if (hasPathFallback(modId)) {
                            usingPathFallback = true;
                            stillLoading = true
                        } else {
                            noLoads.push(modId);
                            removeScript(modId)
                        }
                    } else {
                        if (!mod.inited && mod.fetched && map.isDefine) {
                            stillLoading = true;
                            if (!map.prefix) {
                                return (needCycleCheck = false)
                            }
                        }
                    }
                }
            });
            if (expired && noLoads.length) {
                err = makeError("timeout", "Load timeout for modules: " + noLoads, null, noLoads);
                err.contextName = context.contextName;
                return onError(err)
            }
            if (needCycleCheck) {
                each(reqCalls, function(mod) {
                    breakCycle(mod, {}, {})
                })
            }
            if ((!expired || usingPathFallback) && stillLoading) {
                if ((isBrowser || isWebWorker) && !checkLoadedTimeoutId) {
                    checkLoadedTimeoutId = setTimeout(function() {
                        checkLoadedTimeoutId = 0;
                        checkLoaded()
                    }, 50)
                }
            }
            inCheckLoaded = false
        }
        Module = function(map) {
            this.events = getOwn(undefEvents, map.id) || {};
            this.map = map;
            this.shim = getOwn(config.shim, map.id);
            this.depExports = [];
            this.depMaps = [];
            this.depMatched = [];
            this.pluginMaps = {};
            this.depCount = 0
        };
        Module.prototype = {init: function(depMaps, factory, errback, options) {
                options = options || {};
                if (this.inited) {
                    return
                }
                this.factory = factory;
                if (errback) {
                    this.on("error", errback)
                } else {
                    if (this.events.error) {
                        errback = bind(this, function(err) {
                            this.emit("error", err)
                        })
                    }
                }
                this.depMaps = depMaps && depMaps.slice(0);
                this.errback = errback;
                this.inited = true;
                this.ignore = options.ignore;
                if (options.enabled || this.enabled) {
                    this.enable()
                } else {
                    this.check()
                }
            },defineDep: function(i, depExports) {
                if (!this.depMatched[i]) {
                    this.depMatched[i] = true;
                    this.depCount -= 1;
                    this.depExports[i] = depExports
                }
            },fetch: function() {
                if (this.fetched) {
                    return
                }
                this.fetched = true;
                context.startTime = (new Date()).getTime();
                var map = this.map;
                if (this.shim) {
                    context.makeRequire(this.map, {enableBuildCallback: true})(this.shim.deps || [], bind(this, function() {
                        return map.prefix ? this.callPlugin() : this.load()
                    }))
                } else {
                    return map.prefix ? this.callPlugin() : this.load()
                }
            },load: function() {
                var url = this.map.url;
                if (!urlFetched[url]) {
                    urlFetched[url] = true;
                    context.load(this.map.id, url)
                }
            },check: function() {
                if (!this.enabled || this.enabling) {
                    return
                }
                var err, cjsModule, id = this.map.id, depExports = this.depExports, exports = this.exports, factory = this.factory;
                if (!this.inited) {
                    this.fetch()
                } else {
                    if (this.error) {
                        this.emit("error", this.error)
                    } else {
                        if (!this.defining) {
                            this.defining = true;
                            if (this.depCount < 1 && !this.defined) {
                                if (isFunction(factory)) {
                                    if ((this.events.error && this.map.isDefine) || req.onError !== defaultOnError) {
                                        try {
                                            exports = context.execCb(id, factory, depExports, exports)
                                        } catch (e) {
                                            err = e
                                        }
                                    } else {
                                        exports = context.execCb(id, factory, depExports, exports)
                                    }
                                    if (this.map.isDefine && exports === undefined) {
                                        cjsModule = this.module;
                                        if (cjsModule) {
                                            exports = cjsModule.exports
                                        } else {
                                            if (this.usingExports) {
                                                exports = this.exports
                                            }
                                        }
                                    }
                                    if (err) {
                                        err.requireMap = this.map;
                                        err.requireModules = this.map.isDefine ? [this.map.id] : null;
                                        err.requireType = this.map.isDefine ? "define" : "require";
                                        return onError((this.error = err))
                                    }
                                } else {
                                    exports = factory
                                }
                                this.exports = exports;
                                if (this.map.isDefine && !this.ignore) {
                                    defined[id] = exports;
                                    if (req.onResourceLoad) {
                                        req.onResourceLoad(context, this.map, this.depMaps)
                                    }
                                }
                                cleanRegistry(id);
                                this.defined = true
                            }
                            this.defining = false;
                            if (this.defined && !this.defineEmitted) {
                                this.defineEmitted = true;
                                this.emit("defined", this.exports);
                                this.defineEmitComplete = true
                            }
                        }
                    }
                }
            },callPlugin: function() {
                var map = this.map, id = map.id, pluginMap = makeModuleMap(map.prefix);
                this.depMaps.push(pluginMap);
                on(pluginMap, "defined", bind(this, function(plugin) {
                    var load, normalizedMap, normalizedMod, bundleId = getOwn(bundlesMap, this.map.id), name = this.map.name, parentName = this.map.parentMap ? this.map.parentMap.name : null, localRequire = context.makeRequire(map.parentMap, {enableBuildCallback: true});
                    if (this.map.unnormalized) {
                        if (plugin.normalize) {
                            name = plugin.normalize(name, function(name) {
                                return normalize(name, parentName, true)
                            }) || ""
                        }
                        normalizedMap = makeModuleMap(map.prefix + "!" + name, this.map.parentMap);
                        on(normalizedMap, "defined", bind(this, function(value) {
                            this.init([], function() {
                                return value
                            }, null, {enabled: true,ignore: true})
                        }));
                        normalizedMod = getOwn(registry, normalizedMap.id);
                        if (normalizedMod) {
                            this.depMaps.push(normalizedMap);
                            if (this.events.error) {
                                normalizedMod.on("error", bind(this, function(err) {
                                    this.emit("error", err)
                                }))
                            }
                            normalizedMod.enable()
                        }
                        return
                    }
                    if (bundleId) {
                        this.map.url = context.nameToUrl(bundleId);
                        this.load();
                        return
                    }
                    load = bind(this, function(value) {
                        this.init([], function() {
                            return value
                        }, null, {enabled: true})
                    });
                    load.error = bind(this, function(err) {
                        this.inited = true;
                        this.error = err;
                        err.requireModules = [id];
                        eachProp(registry, function(mod) {
                            if (mod.map.id.indexOf(id + "_unnormalized") === 0) {
                                cleanRegistry(mod.map.id)
                            }
                        });
                        onError(err)
                    });
                    load.fromText = bind(this, function(text, textAlt) {
                        var moduleName = map.name, moduleMap = makeModuleMap(moduleName), hasInteractive = useInteractive;
                        if (textAlt) {
                            text = textAlt
                        }
                        if (hasInteractive) {
                            useInteractive = false
                        }
                        getModule(moduleMap);
                        if (hasProp(config.config, id)) {
                            config.config[moduleName] = config.config[id]
                        }
                        try {
                            req.exec(text)
                        } catch (e) {
                            return onError(makeError("fromtexteval", "fromText eval for " + id + " failed: " + e, e, [id]))
                        }
                        if (hasInteractive) {
                            useInteractive = true
                        }
                        this.depMaps.push(moduleMap);
                        context.completeLoad(moduleName);
                        localRequire([moduleName], load)
                    });
                    plugin.load(map.name, localRequire, load, config)
                }));
                context.enable(pluginMap, this);
                this.pluginMaps[pluginMap.id] = pluginMap
            },enable: function() {
                enabledRegistry[this.map.id] = this;
                this.enabled = true;
                this.enabling = true;
                each(this.depMaps, bind(this, function(depMap, i) {
                    var id, mod, handler;
                    if (typeof depMap === "string") {
                        depMap = makeModuleMap(depMap, (this.map.isDefine ? this.map : this.map.parentMap), false, !this.skipMap);
                        this.depMaps[i] = depMap;
                        handler = getOwn(handlers, depMap.id);
                        if (handler) {
                            this.depExports[i] = handler(this);
                            return
                        }
                        this.depCount += 1;
                        on(depMap, "defined", bind(this, function(depExports) {
                            this.defineDep(i, depExports);
                            this.check()
                        }));
                        if (this.errback) {
                            on(depMap, "error", bind(this, this.errback))
                        }
                    }
                    id = depMap.id;
                    mod = registry[id];
                    if (!hasProp(handlers, id) && mod && !mod.enabled) {
                        context.enable(depMap, this)
                    }
                }));
                eachProp(this.pluginMaps, bind(this, function(pluginMap) {
                    var mod = getOwn(registry, pluginMap.id);
                    if (mod && !mod.enabled) {
                        context.enable(pluginMap, this)
                    }
                }));
                this.enabling = false;
                this.check()
            },on: function(name, cb) {
                var cbs = this.events[name];
                if (!cbs) {
                    cbs = this.events[name] = []
                }
                cbs.push(cb)
            },emit: function(name, evt) {
                each(this.events[name], function(cb) {
                    cb(evt)
                });
                if (name === "error") {
                    delete this.events[name]
                }
            }};
        function callGetModule(args) {
            if (!hasProp(defined, args[0])) {
                getModule(makeModuleMap(args[0], null, true)).init(args[1], args[2])
            }
        }
        function removeListener(node, func, name, ieName) {
            if (node.detachEvent && !isOpera) {
                if (ieName) {
                    node.detachEvent(ieName, func)
                }
            } else {
                node.removeEventListener(name, func, false)
            }
        }
        function getScriptData(evt) {
            var node = evt.currentTarget || evt.srcElement;
            removeListener(node, context.onScriptLoad, "load", "onreadystatechange");
            removeListener(node, context.onScriptError, "error");
            return {node: node,id: node && node.getAttribute("data-requiremodule")}
        }
        function intakeDefines() {
            var args;
            takeGlobalQueue();
            while (defQueue.length) {
                args = defQueue.shift();
                if (args[0] === null) {
                    return onError(makeError("mismatch", "Mismatched anonymous define() module: " + args[args.length - 1]))
                } else {
                    callGetModule(args)
                }
            }
        }
        context = {config: config,contextName: contextName,registry: registry,defined: defined,urlFetched: urlFetched,defQueue: defQueue,Module: Module,makeModuleMap: makeModuleMap,nextTick: req.nextTick,onError: onError,configure: function(cfg) {
                if (cfg.baseUrl) {
                    if (cfg.baseUrl.charAt(cfg.baseUrl.length - 1) !== "/") {
                        cfg.baseUrl += "/"
                    }
                }
                var shim = config.shim, objs = {paths: true,bundles: true,config: true,map: true};
                eachProp(cfg, function(value, prop) {
                    if (objs[prop]) {
                        if (!config[prop]) {
                            config[prop] = {}
                        }
                        mixin(config[prop], value, true, true)
                    } else {
                        config[prop] = value
                    }
                });
                if (cfg.bundles) {
                    eachProp(cfg.bundles, function(value, prop) {
                        each(value, function(v) {
                            if (v !== prop) {
                                bundlesMap[v] = prop
                            }
                        })
                    })
                }
                if (cfg.shim) {
                    eachProp(cfg.shim, function(value, id) {
                        if (isArray(value)) {
                            value = {deps: value}
                        }
                        if ((value.exports || value.init) && !value.exportsFn) {
                            value.exportsFn = context.makeShimExports(value)
                        }
                        shim[id] = value
                    });
                    config.shim = shim
                }
                if (cfg.packages) {
                    each(cfg.packages, function(pkgObj) {
                        var location, name;
                        pkgObj = typeof pkgObj === "string" ? {name: pkgObj} : pkgObj;
                        name = pkgObj.name;
                        location = pkgObj.location;
                        if (location) {
                            config.paths[name] = pkgObj.location
                        }
                        config.pkgs[name] = pkgObj.name + "/" + (pkgObj.main || "main").replace(currDirRegExp, "").replace(jsSuffixRegExp, "")
                    })
                }
                eachProp(registry, function(mod, id) {
                    if (!mod.inited && !mod.map.unnormalized) {
                        mod.map = makeModuleMap(id)
                    }
                });
                if (cfg.deps || cfg.callback) {
                    context.require(cfg.deps || [], cfg.callback)
                }
            },makeShimExports: function(value) {
                function fn() {
                    var ret;
                    if (value.init) {
                        ret = value.init.apply(global, arguments)
                    }
                    return ret || (value.exports && getGlobal(value.exports))
                }
                return fn
            },makeRequire: function(relMap, options) {
                options = options || {};
                function localRequire(deps, callback, errback) {
                    var id, map, requireMod;
                    if (options.enableBuildCallback && callback && isFunction(callback)) {
                        callback.__requireJsBuild = true
                    }
                    if (typeof deps === "string") {
                        if (isFunction(callback)) {
                            return onError(makeError("requireargs", "Invalid require call"), errback)
                        }
                        if (relMap && hasProp(handlers, deps)) {
                            return handlers[deps](registry[relMap.id])
                        }
                        if (req.get) {
                            return req.get(context, deps, relMap, localRequire)
                        }
                        map = makeModuleMap(deps, relMap, false, true);
                        id = map.id;
                        if (!hasProp(defined, id)) {
                            return onError(makeError("notloaded", 'Module name "' + id + '" has not been loaded yet for context: ' + contextName + (relMap ? "" : ". Use require([])")))
                        }
                        return defined[id]
                    }
                    intakeDefines();
                    context.nextTick(function() {
                        intakeDefines();
                        requireMod = getModule(makeModuleMap(null, relMap));
                        requireMod.skipMap = options.skipMap;
                        requireMod.init(deps, callback, errback, {enabled: true});
                        checkLoaded()
                    });
                    return localRequire
                }
                mixin(localRequire, {isBrowser: isBrowser,toUrl: function(moduleNamePlusExt) {
                        var ext, index = moduleNamePlusExt.lastIndexOf("."), segment = moduleNamePlusExt.split("/")[0], isRelative = segment === "." || segment === "..";
                        if (index !== -1 && (!isRelative || index > 1)) {
                            ext = moduleNamePlusExt.substring(index, moduleNamePlusExt.length);
                            moduleNamePlusExt = moduleNamePlusExt.substring(0, index)
                        }
                        return context.nameToUrl(normalize(moduleNamePlusExt, relMap && relMap.id, true), ext, true)
                    },defined: function(id) {
                        return hasProp(defined, makeModuleMap(id, relMap, false, true).id)
                    },specified: function(id) {
                        id = makeModuleMap(id, relMap, false, true).id;
                        return hasProp(defined, id) || hasProp(registry, id)
                    }});
                if (!relMap) {
                    localRequire.undef = function(id) {
                        takeGlobalQueue();
                        var map = makeModuleMap(id, relMap, true), mod = getOwn(registry, id);
                        removeScript(id);
                        delete defined[id];
                        delete urlFetched[map.url];
                        delete undefEvents[id];
                        eachReverse(defQueue, function(args, i) {
                            if (args[0] === id) {
                                defQueue.splice(i, 1)
                            }
                        });
                        if (mod) {
                            if (mod.events.defined) {
                                undefEvents[id] = mod.events
                            }
                            cleanRegistry(id)
                        }
                    }
                }
                return localRequire
            },enable: function(depMap) {
                var mod = getOwn(registry, depMap.id);
                if (mod) {
                    getModule(depMap).enable()
                }
            },completeLoad: function(moduleName) {
                var found, args, mod, shim = getOwn(config.shim, moduleName) || {}, shExports = shim.exports;
                takeGlobalQueue();
                while (defQueue.length) {
                    args = defQueue.shift();
                    if (args[0] === null) {
                        args[0] = moduleName;
                        if (found) {
                            break
                        }
                        found = true
                    } else {
                        if (args[0] === moduleName) {
                            found = true
                        }
                    }
                    callGetModule(args)
                }
                mod = getOwn(registry, moduleName);
                if (!found && !hasProp(defined, moduleName) && mod && !mod.inited) {
                    if (config.enforceDefine && (!shExports || !getGlobal(shExports))) {
                        if (hasPathFallback(moduleName)) {
                            return
                        } else {
                            return onError(makeError("nodefine", "No define call for " + moduleName, null, [moduleName]))
                        }
                    } else {
                        callGetModule([moduleName, (shim.deps || []), shim.exportsFn])
                    }
                }
                checkLoaded()
            },nameToUrl: function(moduleName, ext, skipExt) {
                var paths, syms, i, parentModule, url, parentPath, bundleId, pkgMain = getOwn(config.pkgs, moduleName);
                if (pkgMain) {
                    moduleName = pkgMain
                }
                bundleId = getOwn(bundlesMap, moduleName);
                if (bundleId) {
                    return context.nameToUrl(bundleId, ext, skipExt)
                }
                if (req.jsExtRegExp.test(moduleName)) {
                    url = moduleName + (ext || "")
                } else {
                    paths = config.paths;
                    syms = moduleName.split("/");
                    for (i = syms.length; i > 0; i -= 1) {
                        parentModule = syms.slice(0, i).join("/");
                        parentPath = getOwn(paths, parentModule);
                        if (parentPath) {
                            if (isArray(parentPath)) {
                                parentPath = parentPath[0]
                            }
                            syms.splice(0, i, parentPath);
                            break
                        }
                    }
                    url = syms.join("/");
                    url += (ext || (/^data\:|\?/.test(url) || skipExt ? "" : ".js"));
                    url = (url.charAt(0) === "/" || url.match(/^[\w\+\.\-]+:/) ? "" : config.baseUrl) + url
                }
                return config.urlArgs ? url + ((url.indexOf("?") === -1 ? "?" : "&") + config.urlArgs) : url
            },load: function(id, url) {
                req.load(context, id, url)
            },execCb: function(name, callback, args, exports) {
                return callback.apply(exports, args)
            },onScriptLoad: function(evt) {
                if (evt.type === "load" || (readyRegExp.test((evt.currentTarget || evt.srcElement).readyState))) {
                    interactiveScript = null;
                    var data = getScriptData(evt);
                    context.completeLoad(data.id)
                }
            },onScriptError: function(evt) {
                var data = getScriptData(evt);
                if (!hasPathFallback(data.id)) {
                    return onError(makeError("scripterror", "Script error for: " + data.id, evt, [data.id]))
                }
            }};
        context.require = context.makeRequire();
        return context
    }
    req = requirejs = function(deps, callback, errback, optional) {
        var context, config, contextName = defContextName;
        if (!isArray(deps) && typeof deps !== "string") {
            config = deps;
            if (isArray(callback)) {
                deps = callback;
                callback = errback;
                errback = optional
            } else {
                deps = []
            }
        }
        if (config && config.context) {
            contextName = config.context
        }
        context = getOwn(contexts, contextName);
        if (!context) {
            context = contexts[contextName] = req.s.newContext(contextName)
        }
        if (config) {
            context.configure(config)
        }
        return context.require(deps, callback, errback)
    };
    req.config = function(config) {
        return req(config)
    };
    req.nextTick = typeof setTimeout !== "undefined" ? function(fn) {
        setTimeout(fn, 4)
    } : function(fn) {
        fn()
    };
    if (!require) {
        require = req
    }
    req.version = version;
    req.jsExtRegExp = /^\/|:|\?|\.js$/;
    req.isBrowser = isBrowser;
    s = req.s = {contexts: contexts,newContext: newContext};
    req({});
    each(["toUrl", "undef", "defined", "specified"], function(prop) {
        req[prop] = function() {
            var ctx = contexts[defContextName];
            return ctx.require[prop].apply(ctx, arguments)
        }
    });
    if (isBrowser) {
        head = s.head = document.getElementsByTagName("head")[0];
        baseElement = document.getElementsByTagName("base")[0];
        if (baseElement) {
            head = s.head = baseElement.parentNode
        }
    }
    req.onError = defaultOnError;
    req.createNode = function(config, moduleName, url) {
        var node = config.xhtml ? document.createElementNS("http://www.w3.org/1999/xhtml", "html:script") : document.createElement("script");
        node.type = config.scriptType || "text/javascript";
        node.charset = "utf-8";
        node.async = true;
        return node
    };
    req.load = function(context, moduleName, url) {
        var config = (context && context.config) || {}, node;
        if (isBrowser) {
            node = req.createNode(config, moduleName, url);
            node.setAttribute("data-requirecontext", context.contextName);
            node.setAttribute("data-requiremodule", moduleName);
            if (node.attachEvent && !(node.attachEvent.toString && node.attachEvent.toString().indexOf("[native code") < 0) && !isOpera) {
                useInteractive = true;
                node.attachEvent("onreadystatechange", context.onScriptLoad)
            } else {
                node.addEventListener("load", context.onScriptLoad, false);
                node.addEventListener("error", context.onScriptError, false)
            }
            node.src = url;
            currentlyAddingScript = node;
            if (baseElement) {
                head.insertBefore(node, baseElement)
            } else {
                head.appendChild(node)
            }
            currentlyAddingScript = null;
            return node
        } else {
            if (isWebWorker) {
                try {
                    importScripts(url);
                    context.completeLoad(moduleName)
                } catch (e) {
                    context.onError(makeError("importscripts", "importScripts failed for " + moduleName + " at " + url, e, [moduleName]))
                }
            }
        }
    };
    function getInteractiveScript() {
        if (interactiveScript && interactiveScript.readyState === "interactive") {
            return interactiveScript
        }
        eachReverse(scripts(), function(script) {
            if (script.readyState === "interactive") {
                return (interactiveScript = script)
            }
        });
        return interactiveScript
    }
    if (isBrowser && !cfg.skipDataMain) {
        eachReverse(scripts(), function(script) {
            if (!head) {
                head = script.parentNode
            }
            dataMain = script.getAttribute("data-main");
            if (dataMain) {
                mainScript = dataMain;
                if (!cfg.baseUrl) {
                    src = mainScript.split("/");
                    mainScript = src.pop();
                    subPath = src.length ? src.join("/") + "/" : "./";
                    cfg.baseUrl = subPath
                }
                mainScript = mainScript.replace(jsSuffixRegExp, "");
                if (req.jsExtRegExp.test(mainScript)) {
                    mainScript = dataMain
                }
                cfg.deps = cfg.deps ? cfg.deps.concat(mainScript) : [mainScript];
                return true
            }
        })
    }
    define = function(name, deps, callback) {
        var node, context;
        if (typeof name !== "string") {
            callback = deps;
            deps = name;
            name = null
        }
        if (!isArray(deps)) {
            callback = deps;
            deps = null
        }
        if (!deps && isFunction(callback)) {
            deps = [];
            if (callback.length) {
                callback.toString().replace(commentRegExp, "").replace(cjsRequireRegExp, function(match, dep) {
                    deps.push(dep)
                });
                deps = (callback.length === 1 ? ["require"] : ["require", "exports", "module"]).concat(deps)
            }
        }
        if (useInteractive) {
            node = currentlyAddingScript || getInteractiveScript();
            if (node) {
                if (!name) {
                    name = node.getAttribute("data-requiremodule")
                }
                context = contexts[node.getAttribute("data-requirecontext")]
            }
        }
        (context ? context.defQueue : globalDefQueue).push([name, deps, callback])
    };
    define.amd = {jQuery: true};
    req.exec = function(text) {
        return eval(text)
    };
    req(cfg)
}(this));
requirejs.config({baseUrl: URLPrefix.statics + "/global/js",paths: {qrcode: "libs/moduleLib/qrcode.min.js?v1.01"}});
var loli = {_loli: loli};
(function() {
    var d = window.loli || (window.loli = {});
    var c = d;
    var b = c.util = c.util || {};
    function a(f) {
        if (!f) {
            return true
        }
        for (var e in f) {
            return false
        }
        return true
    }
    b.url = {getParams: function(f) {
            f = $.trim(f);
            var g = this;
            var e = g.parseUrl(f);
            return e ? e.params : null
        },appendParams: function(g, i) {
            var h = this;
            if (a(i)) {
                return g
            }
            var e = h.parseUrl(g);
            if (!e) {
                return g
            }
            var j = e.params;
            for (var f in i) {
                if (i.hasOwnProperty(f) && i[f]) {
                    j[f] = i[f]
                }
            }
            e.params = j;
            return h.toCusString(e)
        },parseUrl: function(m) {
            var k = "";
            var o = "";
            var q = {};
            m = $.trim(m);
            if (m == "") {
                return null
            }
            var h = m.split("#");
            var r = h[0];
            if (h.length >= 2) {
                for (var g = 1, p = h.length; g < p; g++) {
                    k += "#" + h[g]
                }
            }
            var j = r.split("?");
            o = j[0];
            var l = j[1];
            if (l) {
                var t = l.split("&");
                for (var g = 0, p = t.length; g < p; g++) {
                    var f = t[g].indexOf("=");
                    if (f == -1) {
                        continue
                    }
                    var e = t[g].substring(0, f);
                    var s = t[g].substring(f + 1);
                    q[e] = s
                }
            }
            var n = {loc: o,params: q,append: k};
            return n
        },toCusString: function(i) {
            var f = [];
            f.push(i.loc);
            var e = i.params;
            if (!a(e)) {
                f.push("?");
                var g = 0;
                for (var h in e) {
                    if (e.hasOwnProperty(h) && e[h]) {
                        if (g) {
                            f.push("&")
                        }
                        f.push(h + "=" + e[h]);
                        g++
                    }
                }
            }
            if (i.append) {
                f.push(i.append)
            }
            return f.join("")
        }}
})();
(function() {
    var b = window.loli || (window.loli = {});
    var a = b;
    a.config = a.config || {};
    a.config.genUID = function() {
        var d = new Date().getTime();
        var h = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ`|abcdefghijklmnopqrstuvwxyz".split("");
        var g = [];
        var f = c(d);
        var e = f.length;
        for (var j = 0; j < e; j++) {
            g.push(h[parseInt(f[j], 2).toString(10)])
        }
        return g.join("");
        function c(l) {
            var i = l.toString(2);
            var k = i.length;
            var m = [];
            var o = k % 6;
            if (o < 0) {
                m.push(i.substring(0, o))
            }
            var n = o;
            while (n < k) {
                m.push(i.substring(n, n + 6));
                n += 6
            }
            return m
        }
    }
})();
loli.global = loli.global || {};
loli.global.uid = loli.config.genUID();
var template = function(b, a) {
    return template[typeof a === "object" ? "render" : "compile"].apply(template, arguments)
};
(function(a, c) {
    a.version = "2.0.1";
    a.openTag = "<!%";
    a.closeTag = "%!>";
    a.isEscape = true;
    a.isCompress = false;
    a.parser = null;
    a.render = function(i, h) {
        var g = f(i);
        if (g === undefined) {
            return d({id: i,name: "Render Error",message: "No Template"})
        }
        return g(h)
    };
    a.compile = function(k, n) {
        var j = arguments;
        var m = j[2];
        var i = "anonymous";
        if (typeof n !== "string") {
            m = j[1];
            n = j[0];
            k = i
        }
        try {
            var l = b(n, m)
        } catch (h) {
            h.id = k || n;
            h.name = "Syntax Error";
            return d(h)
        }
        function g(p) {
            try {
                return new l(p) + ""
            } catch (o) {
                if (!m) {
                    return a.compile(k, n, true)(p)
                }
                o.id = k || n;
                o.name = "Render Error";
                o.source = n;
                return d(o)
            }
        }
        g.prototype = l.prototype;
        g.toString = function() {
            return l.toString()
        };
        if (k !== i) {
            e[k] = g
        }
        return g
    };
    a.helper = function(g, h) {
        a.prototype[g] = h
    };
    a.onerror = function(h) {
        var g = "[template]:\n" + h.id + "\n\n[name]:\n" + h.name;
        if (h.message) {
            g += "\n\n[message]:\n" + h.message
        }
        if (h.line) {
            g += "\n\n[line]:\n" + h.line;
            g += "\n\n[source]:\n" + h.source.split(/\n/)[h.line - 1].replace(/^[\s\t]+/, "")
        }
        if (h.temp) {
            g += "\n\n[temp]:\n" + h.temp
        }
        if (c.console) {
            console.error(g)
        }
    };
    var e = {};
    var f = function(h) {
        var i = e[h];
        if (i === undefined && "document" in c) {
            var j = document.getElementById(h);
            if (j) {
                var g = j.value || j.innerHTML;
                return a.compile(h, g.replace(/^\s*|\s*$/g, ""))
            }
        } else {
            if (e.hasOwnProperty(h)) {
                return i
            }
        }
    };
    var d = function(h) {
        a.onerror(h);
        function g() {
            return g + ""
        }
        g.toString = function() {
            return "{Template Error}"
        };
        return g
    };
    var b = (function() {
        a.prototype = {$render: a.render,$escape: function(p) {
                return typeof p === "string" ? p.replace(/&(?![\w#]+;)|[<>"']/g, function(q) {
                    return {"<": "&#60;",">": "&#62;",'"': "&#34;","'": "&#39;","&": "&#38;"}[q]
                }) : p
            },$string: function(p) {
                if (typeof p === "string" || typeof p === "number") {
                    return p
                } else {
                    if (typeof p === "function") {
                        return p()
                    } else {
                        return ""
                    }
                }
            }};
        var n = Array.prototype.forEach || function(r, p) {
            var s = this.length >>> 0;
            for (var q = 0; q < s; q++) {
                if (q in this) {
                    r.call(p, this[q], q, this)
                }
            }
        };
        var i = function(p, q) {
            n.call(p, q)
        };
        var l = "break,case,catch,continue,debugger,default,delete,do,else,false,finally,for,function,if,in,instanceof,new,null,return,switch,this,throw,true,try,typeof,var,void,while,with,abstract,boolean,byte,char,class,const,double,enum,export,extends,final,float,goto,implements,import,int,interface,long,native,package,private,protected,public,short,static,super,synchronized,throws,transient,volatile,arguments,let,yield,undefined";
        var k = /\/\*(?:.|\n)*?\*\/|\/\/[^\n]*\n|\/\/[^\n]*$|'[^']*'|"[^"]*"|[\s\t\n]*\.[\s\t\n]*[$\w\.]+/g;
        var m = /[^\w$]+/g;
        var g = new RegExp(["\\b" + l.replace(/,/g, "\\b|\\b") + "\\b"].join("|"), "g");
        var h = /\b\d[^,]*/g;
        var j = /^,+|,+$/g;
        var o = function(p) {
            p = p.replace(k, "").replace(m, ",").replace(g, "").replace(h, "").replace(j, "");
            p = p ? p.split(/,+/) : [];
            return p
        };
        return function(I, z) {
            var s = a.openTag;
            var p = a.closeTag;
            var E = a.parser;
            var H = I;
            var A = "";
            var G = 1;
            var w = {$data: true,$helpers: true,$out: true,$line: true};
            var r = a.prototype;
            var J = {};
            var D = "var $helpers=this," + (z ? "$line=0," : "");
            var B = "".trim;
            var t = B ? ["$out='';", "$out+=", ";", "$out"] : ["$out=[];", "$out.push(", ");", "$out.join('')"];
            var v = B ? "if(content!==undefined){$out+=content;return content}" : "$out.push(content);";
            var K = "function(content){" + v + "}";
            var x = "function(id,data){if(data===undefined){data=$data}var content=$helpers.$render(id,data);" + v + "}";
            i(H.split(s), function(P, O) {
                P = P.split(p);
                var N = P[0];
                var M = P[1];
                if (P.length === 1) {
                    A += F(N)
                } else {
                    A += L(N);
                    if (M) {
                        A += F(M)
                    }
                }
            });
            H = A;
            if (z) {
                H = "try{" + H + "}catch(e){e.line=$line;throw e}"
            }
            H = "'use strict';" + D + t[0] + H + "return new String(" + t[3] + ");";
            try {
                var q = new Function("$data", H);
                q.prototype = J;
                return q
            } catch (y) {
                y.temp = "function anonymous($data) {" + H + "}";
                throw y
            }
            function F(M) {
                G += M.split(/\n/).length - 1;
                if (a.isCompress) {
                    M = M.replace(/[\n\r\t\s]+/g, " ")
                }
                M = M.replace(/('|\\)/g, "\\$1").replace(/\r/g, "\\r").replace(/\n/g, "\\n");
                M = t[1] + "'" + M + "'" + t[2];
                return M + "\n"
            }
            function L(O) {
                var P = G;
                if (E) {
                    O = E(O)
                } else {
                    if (z) {
                        O = O.replace(/\n/g, function() {
                            G++;
                            return "$line=" + G + ";"
                        })
                    }
                }
                if (O.indexOf("=") === 0) {
                    var N = O.indexOf("==") !== 0;
                    O = O.replace(/^=*|[\s;]*$/g, "");
                    if (N && a.isEscape) {
                        var M = O.replace(/\s*\([^\)]+\)/, "");
                        if (!r.hasOwnProperty(M) && !/^(include|print)$/.test(M)) {
                            O = "$escape($string(" + O + "))"
                        }
                    } else {
                        O = "$string(" + O + ")"
                    }
                    O = t[1] + O + t[2]
                }
                if (z) {
                    O = "$line=" + P + ";" + O
                }
                C(O);
                return O + "\n"
            }
            function C(M) {
                M = o(M);
                i(M, function(N) {
                    if (!w.hasOwnProperty(N)) {
                        u(N);
                        w[N] = true
                    }
                })
            }
            function u(M) {
                var N;
                if (M === "print") {
                    N = K
                } else {
                    if (M === "include") {
                        J["$render"] = r["$render"];
                        N = x
                    } else {
                        N = "$data." + M;
                        if (r.hasOwnProperty(M)) {
                            J[M] = r[M];
                            if (M.indexOf("$") === 0) {
                                N = "$helpers." + M
                            } else {
                                N = N + "===undefined?$helpers." + M + ":" + N
                            }
                        }
                    }
                }
                D += M + "=" + N + ","
            }
        }
    })()
})(template, this);
(function(cg, aP) {
    var bg = cg.document;
    var be = (function() {
        var F = function(J, I) {
            return new F.fn.init(J, I)
        }, g = cg.jQuery, D = cg.$, H, a = /^(?:[^<]*(<[\w\W]+>)[^>]*$|#([\w\-]+)$)/, m = /^.[^:#\[\.,]*$/, w = /\S/, i = /\s/, B = /^\s+/, G = /\s+$/, s = /\W/, C = /\d/, f = /^<(\w+)\s*\/?>(?:<\/\1>)?$/, u = /^[\],:{}\s]*$/, c = /\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g, q = /"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, A = /(?:^|:|,)(?:\s*\[)+/g, o = /(webkit)[ \/]([\w.]+)/, l = /(opera)(?:.*version)?[ \/]([\w.]+)/, n = /(msie) ([\w.]+)/, k = /(mozilla)(?:.*? rv:([\w.]+))?/, b = navigator.userAgent, e, h = false, d = [], x, y = Object.prototype.toString, E = Object.prototype.hasOwnProperty, j = Array.prototype.push, z = Array.prototype.slice, r = String.prototype.trim, v = Array.prototype.indexOf, p = {};
        F.fn = F.prototype = {init: function(N, K) {
                var L, J, M, I;
                if (!N) {
                    return this
                }
                if (N.nodeType) {
                    this.context = this[0] = N;
                    this.length = 1;
                    return this
                }
                if (N === "body" && !K && bg.body) {
                    this.context = bg;
                    this[0] = bg.body;
                    this.selector = "body";
                    this.length = 1;
                    return this
                }
                if (typeof N === "string") {
                    L = a.exec(N);
                    if (L && (L[1] || !K)) {
                        if (L[1]) {
                            I = (K ? K.ownerDocument || K : bg);
                            M = f.exec(N);
                            if (M) {
                                if (F.isPlainObject(K)) {
                                    N = [bg.createElement(M[1])];
                                    F.fn.attr.call(N, K, true)
                                } else {
                                    N = [I.createElement(M[1])]
                                }
                            } else {
                                M = F.buildFragment([L[1]], [I]);
                                N = (M.cacheable ? M.fragment.cloneNode(true) : M.fragment).childNodes
                            }
                            return F.merge(this, N)
                        } else {
                            J = bg.getElementById(L[2]);
                            if (J && J.parentNode) {
                                if (J.id !== L[2]) {
                                    return H.find(N)
                                }
                                this.length = 1;
                                this[0] = J
                            }
                            this.context = bg;
                            this.selector = N;
                            return this
                        }
                    } else {
                        if (!K && !s.test(N)) {
                            this.selector = N;
                            this.context = bg;
                            N = bg.getElementsByTagName(N);
                            return F.merge(this, N)
                        } else {
                            if (!K || K.jquery) {
                                return (K || H).find(N)
                            } else {
                                return F(K).find(N)
                            }
                        }
                    }
                } else {
                    if (F.isFunction(N)) {
                        return H.ready(N)
                    }
                }
                if (N.selector !== aP) {
                    this.selector = N.selector;
                    this.context = N.context
                }
                return F.makeArray(N, this)
            },selector: "",jquery: "1.4.4",length: 0,size: function() {
                return this.length
            },toArray: function() {
                return z.call(this, 0)
            },get: function(I) {
                return I == null ? this.toArray() : (I < 0 ? this.slice(I)[0] : this[I])
            },pushStack: function(K, I, L) {
                var J = F();
                if (F.isArray(K)) {
                    j.apply(J, K)
                } else {
                    F.merge(J, K)
                }
                J.prevObject = this;
                J.context = this.context;
                if (I === "find") {
                    J.selector = this.selector + (this.selector ? " " : "") + L
                } else {
                    if (I) {
                        J.selector = this.selector + "." + I + "(" + L + ")"
                    }
                }
                return J
            },each: function(I, J) {
                return F.each(this, I, J)
            },ready: function(I) {
                F.bindReady();
                if (F.isReady) {
                    I.call(bg, F)
                } else {
                    if (d) {
                        d.push(I)
                    }
                }
                return this
            },eq: function(I) {
                return I === -1 ? this.slice(I) : this.slice(I, +I + 1)
            },first: function() {
                return this.eq(0)
            },last: function() {
                return this.eq(-1)
            },slice: function() {
                return this.pushStack(z.apply(this, arguments), "slice", z.call(arguments).join(","))
            },map: function(I) {
                return this.pushStack(F.map(this, function(J, K) {
                    return I.call(J, K, J)
                }))
            },end: function() {
                return this.prevObject || F(null)
            },push: j,sort: [].sort,splice: [].splice};
        F.fn.init.prototype = F.fn;
        F.extend = F.fn.extend = function() {
            var L, I, K, J, O, N, P = arguments[0] || {}, Q = 1, R = arguments.length, M = false;
            if (typeof P === "boolean") {
                M = P;
                P = arguments[1] || {};
                Q = 2
            }
            if (typeof P !== "object" && !F.isFunction(P)) {
                P = {}
            }
            if (R === Q) {
                P = this;
                --Q
            }
            for (; Q < R; Q++) {
                if ((L = arguments[Q]) != null) {
                    for (I in L) {
                        K = P[I];
                        J = L[I];
                        if (P === J) {
                            continue
                        }
                        if (M && J && (F.isPlainObject(J) || (O = F.isArray(J)))) {
                            if (O) {
                                O = false;
                                N = K && F.isArray(K) ? K : []
                            } else {
                                N = K && F.isPlainObject(K) ? K : {}
                            }
                            P[I] = F.extend(M, N, J)
                        } else {
                            if (J !== aP) {
                                P[I] = J
                            }
                        }
                    }
                }
            }
            return P
        };
        F.extend({noConflict: function(I) {
                cg.$ = D;
                if (I) {
                    cg.jQuery = g
                }
                return F
            },isReady: false,readyWait: 1,ready: function(I) {
                if (I === true) {
                    F.readyWait--
                }
                ilog(' F.readyWait--:'+F.readyWait);
                ilog(' I:'+I);
                ilog(' F.isReady:'+F.isReady);
                if (!F.readyWait || (I !== true && !F.isReady)) {
                	ilog('bg.body:');
                    if (!bg.body) {
                        return setTimeout(F.ready, 1)
                    }
                    ilog(' F.readyWait--');
                    F.isReady = true;
                    if (I !== true && --F.readyWait > 0) {
                        return
                    }
                    if (d) {
                        var J, L = 0, K = d;
                        d = null;
                        while ((J = K[L++])) {
                        	ilog(' J.call(bg, F)...');
                            J.call(bg, F)
                        }
                        if (F.fn.trigger) {
                            F(bg).trigger("ready").unbind("ready")
                        }
                    }
                }
            },bindReady: function() {
                if (h) {
                    return
                }
                h = true;
                if (bg.readyState === "complete") {
                    return setTimeout(F.ready, 1)
                }
                ilog('bg.addEventListener..');
                if (bg.addEventListener) {
                    bg.addEventListener("DOMContentLoaded", x, false);
                    cg.addEventListener("load", F.ready, false)
                } else {
                    if (bg.attachEvent) {
                        bg.attachEvent("onreadystatechange", x);
                        cg.attachEvent("onload", F.ready);
                        var J = false;
                        try {
                            J = cg.frameElement == null
                        } catch (I) {
                        }
                        if (bg.documentElement.doScroll && J) {
                            t()
                        }
                    }
                }
            },isFunction: function(I) {
                return F.type(I) === "function"
            },isArray: Array.isArray || function(I) {
                return F.type(I) === "array"
            },isWindow: function(I) {
                return I && typeof I === "object" && "setInterval" in I
            },isNaN: function(I) {
                return I == null || !C.test(I) || isNaN(I)
            },type: function(I) {
                return I == null ? String(I) : p[y.call(I)] || "object"
            },isPlainObject: function(I) {
                if (!I || F.type(I) !== "object" || I.nodeType || F.isWindow(I)) {
                    return false
                }
                if (I.constructor && !E.call(I, "constructor") && !E.call(I.constructor.prototype, "isPrototypeOf")) {
                    return false
                }
                var J;
                for (J in I) {
                }
                return J === aP || E.call(I, J)
            },isEmptyObject: function(I) {
                for (var J in I) {
                    return false
                }
                return true
            },error: function(I) {
                throw I
            },parseJSON: function(I) {
                if (typeof I !== "string" || !I) {
                    return null
                }
                I = F.trim(I);
                if (u.test(I.replace(c, "@").replace(q, "]").replace(A, ""))) {
                    return cg.JSON && cg.JSON.parse ? cg.JSON.parse(I) : (new Function("return " + I))()
                } else {
                    F.error("Invalid JSON: " + I)
                }
            },noop: function() {
            },globalEval: function(I) {
                if (I && w.test(I)) {
                    var J = bg.getElementsByTagName("head")[0] || bg.documentElement, K = bg.createElement("script");
                    K.type = "text/javascript";
                    if (F.support.scriptEval) {
                        K.appendChild(bg.createTextNode(I))
                    } else {
                        K.text = I
                    }
                    J.insertBefore(K, J.firstChild);
                    J.removeChild(K)
                }
            },nodeName: function(I, J) {
                return I.nodeName && I.nodeName.toUpperCase() === J.toUpperCase()
            },each: function(M, I, N) {
                var O, L = 0, K = M.length, P = K === aP || F.isFunction(M);
                if (N) {
                    if (P) {
                        for (O in M) {
                            if (I.apply(M[O], N) === false) {
                                break
                            }
                        }
                    } else {
                        for (; L < K; ) {
                            if (I.apply(M[L++], N) === false) {
                                break
                            }
                        }
                    }
                } else {
                    if (P) {
                        for (O in M) {
                            if (I.call(M[O], O, M[O]) === false) {
                                break
                            }
                        }
                    } else {
                        for (var J = M[0]; L < K && I.call(J, L, J) !== false; J = M[++L]) {
                        }
                    }
                }
                return M
            },trim: r ? function(I) {
                return I == null ? "" : r.call(I)
            } : function(I) {
                return I == null ? "" : I.toString().replace(B, "").replace(G, "")
            },makeArray: function(I, K) {
                var L = K || [];
                if (I != null) {
                    var J = F.type(I);
                    if (I.length == null || J === "string" || J === "function" || J === "regexp" || F.isWindow(I)) {
                        j.call(L, I)
                    } else {
                        F.merge(L, I)
                    }
                }
                return L
            },inArray: function(J, I) {
                if (I.indexOf) {
                    return I.indexOf(J)
                }
                for (var L = 0, K = I.length; L < K; L++) {
                    if (I[L] === J) {
                        return L
                    }
                }
                return -1
            },merge: function(I, K) {
                var J = I.length, L = 0;
                if (typeof K.length === "number") {
                    for (var M = K.length; L < M; L++) {
                        I[J++] = K[L]
                    }
                } else {
                    while (K[L] !== aP) {
                        I[J++] = K[L++]
                    }
                }
                I.length = J;
                return I
            },grep: function(N, I, O) {
                var M = [], J;
                O = !!O;
                for (var L = 0, K = N.length; L < K; L++) {
                    J = !!I(N[L], L);
                    if (O !== J) {
                        M.push(N[L])
                    }
                }
                return M
            },map: function(N, I, O) {
                var M = [], J;
                for (var L = 0, K = N.length; L < K; L++) {
                    J = I(N[L], L, O);
                    if (J != null) {
                        M[M.length] = J
                    }
                }
                return M.concat.apply([], M)
            },guid: 1,proxy: function(I, J, K) {
                if (arguments.length === 2) {
                    if (typeof J === "string") {
                        K = I;
                        I = K[J];
                        J = aP
                    } else {
                        if (J && !F.isFunction(J)) {
                            K = J;
                            J = aP
                        }
                    }
                }
                if (!J && I) {
                    J = function() {
                        return I.apply(K || this, arguments)
                    }
                }
                if (I) {
                    J.guid = I.guid = I.guid || J.guid || F.guid++
                }
                return J
            },access: function(K, L, N, I, O, M) {
                var J = K.length;
                if (typeof L === "object") {
                    for (var Q in L) {
                        F.access(K, Q, L[Q], I, O, N)
                    }
                    return K
                }
                if (N !== aP) {
                    I = !M && I && F.isFunction(N);
                    for (var P = 0; P < J; P++) {
                        O(K[P], L, I ? N.call(K[P], P, O(K[P], L)) : N, M)
                    }
                    return K
                }
                return J ? O(K[0], L) : aP
            },now: function() {
                return (new Date()).getTime()
            },uaMatch: function(I) {
                I = I.toLowerCase();
                var J = o.exec(I) || l.exec(I) || n.exec(I) || I.indexOf("compatible") < 0 && k.exec(I) || [];
                return {browser: J[1] || "",version: J[2] || "0"}
            },browser: {}});
        F.each("Boolean Number String Function Array Date RegExp Object".split(" "), function(I, J) {
            p["[object " + J + "]"] = J.toLowerCase()
        });
        e = F.uaMatch(b);
        if (e.browser) {
            F.browser[e.browser] = true;
            F.browser.version = e.version
        }
        if (F.browser.webkit) {
            F.browser.safari = true
        }
        if (v) {
            F.inArray = function(J, I) {
                return v.call(I, J)
            }
        }
        if (!i.test("\xA0")) {
            B = /^[\s\xA0]+/;
            G = /[\s\xA0]+$/
        }
        H = F(bg);
        if (bg.addEventListener) {
        	ilog('bg.addEventListener.x');
            x = function() {
                bg.removeEventListener("DOMContentLoaded", x, false);
                ilog('bg.addEventListener. F.ready()');
                F.ready()
            }
        } else {
            if (bg.attachEvent) {
                x = function() {
                    if (bg.readyState === "complete") {
                        bg.detachEvent("onreadystatechange", x);
                        F.ready()
                    }
                }
            }
        }
        function t() {
            if (F.isReady) {
                return
            }
            try {
                bg.documentElement.doScroll("left")
            } catch (I) {
                setTimeout(t, 1);
                return
            }
            F.ready()
        }
        return (cg.jQuery = cg.$ = F)
    })();
    (function() {
        be.support = {};
        var e = bg.documentElement, f = bg.createElement("script"), l = bg.createElement("div"), k = "script" + be.now();
        l.style.display = "none";
        l.innerHTML = "   <link/><table></table><a href='/a' style='color:red;float:left;opacity:.55;'>a</a><input type='checkbox'/>";
        var b = l.getElementsByTagName("*"), d = l.getElementsByTagName("a")[0], c = bg.createElement("select"), j = c.appendChild(bg.createElement("option"));
        ilog('b:'+b);
        ilog('b.length:'+b.length);
        ilog('d:'+d);
        if (!b || !b.length || !d) {
            return
        }
        be.support = {leadingWhitespace: l.firstChild.nodeType === 3,tbody: !l.getElementsByTagName("tbody").length,htmlSerialize: !!l.getElementsByTagName("link").length,style: /red/.test(d.getAttribute("style")),hrefNormalized: d.getAttribute("href") === "/a",opacity: /^0.55$/.test(d.style.opacity),cssFloat: !!d.style.cssFloat,checkOn: l.getElementsByTagName("input")[0].value === "on",optSelected: j.selected,deleteExpando: true,optDisabled: false,checkClone: false,scriptEval: false,noCloneEvent: true,boxModel: null,inlineBlockNeedsLayout: false,shrinkWrapBlocks: false,reliableHiddenOffsets: true};
        c.disabled = true;
        be.support.optDisabled = !j.disabled;
        f.type = "text/javascript";
        try {
            f.appendChild(bg.createTextNode("window." + k + "=1;"))
        } catch (h) {
        }
        e.insertBefore(f, e.firstChild);
        if (cg[k]) {
            be.support.scriptEval = true;
            delete cg[k]
        }
        try {
            delete f.test
        } catch (h) {
            be.support.deleteExpando = false
        }
        e.removeChild(f);
        if (l.attachEvent && l.fireEvent) {
            l.attachEvent("onclick", function a() {
                be.support.noCloneEvent = false;
                l.detachEvent("onclick", a)
            });
            l.cloneNode(true).fireEvent("onclick")
        }
        l = bg.createElement("div");
        l.innerHTML = "<input type='radio' name='radiotest' checked='checked'/>";
        var i = bg.createDocumentFragment();
        i.appendChild(l.firstChild);
        be.support.checkClone = i.cloneNode(true).cloneNode(true).lastChild.checked;
        be(function() {
            var m = bg.createElement("div");
            m.style.width = m.style.paddingLeft = "1px";
            bg.body.appendChild(m);
            be.boxModel = be.support.boxModel = m.offsetWidth === 2;
            if ("zoom" in m.style) {
                m.style.display = "inline";
                m.style.zoom = 1;
                be.support.inlineBlockNeedsLayout = m.offsetWidth === 2;
                m.style.display = "";
                m.innerHTML = "<div style='width:4px;'></div>";
                be.support.shrinkWrapBlocks = m.offsetWidth !== 2
            }
            m.innerHTML = "<table><tr><td style='padding:0;display:none'></td><td>t</td></tr></table>";
            var n = m.getElementsByTagName("td");
            be.support.reliableHiddenOffsets = n[0].offsetHeight === 0;
            n[0].style.display = "";
            n[1].style.display = "none";
            be.support.reliableHiddenOffsets = be.support.reliableHiddenOffsets && n[0].offsetHeight === 0;
            m.innerHTML = "";
            bg.body.removeChild(m).style.display = "none";
            m = n = null
        });
        var g = function(n) {
            var o = bg.createElement("div");
            n = "on" + n;
            var m = (n in o);
            if (!m) {
                o.setAttribute(n, "return;");
                m = typeof o[n] === "function"
            }
            o = null;
            return m
        };
        be.support.submitBubbles = g("submit");
        be.support.changeBubbles = g("change");
        e = f = l = b = d = null
    })();
    var b9 = {}, bU = /^(?:\{.*\}|\[.*\])$/;
    be.extend({cache: {},uuid: 0,expando: "jQuery" + be.now(),noData: {embed: true,object: "clsid:D27CDB6E-AE6D-11cf-96B8-444553540000",applet: true},data: function(b, c, f) {
            if (!be.acceptData(b)) {
                return
            }
            b = b == cg ? b9 : b;
            var g = b.nodeType, e = g ? b[be.expando] : null, d = be.cache, a;
            if (g && !e && typeof c === "string" && f === aP) {
                return
            }
            if (!g) {
                d = b
            } else {
                if (!e) {
                    b[be.expando] = e = ++be.uuid
                }
            }
            if (typeof c === "object") {
                if (g) {
                    d[e] = be.extend(d[e], c)
                } else {
                    be.extend(d, c)
                }
            } else {
                if (g && !d[e]) {
                    d[e] = {}
                }
            }
            a = g ? d[e] : d;
            if (f !== aP) {
                a[c] = f
            }
            return typeof c === "string" ? a[c] : a
        },removeData: function(b, c) {
            if (!be.acceptData(b)) {
                return
            }
            b = b == cg ? b9 : b;
            var g = b.nodeType, e = g ? b[be.expando] : b, d = be.cache, a = g ? d[e] : e;
            if (c) {
                if (a) {
                    delete a[c];
                    if (g && be.isEmptyObject(a)) {
                        be.removeData(b)
                    }
                }
            } else {
                if (g && be.support.deleteExpando) {
                    delete b[be.expando]
                } else {
                    if (b.removeAttribute) {
                        b.removeAttribute(be.expando)
                    } else {
                        if (g) {
                            delete d[e]
                        } else {
                            for (var f in b) {
                                delete b[f]
                            }
                        }
                    }
                }
            }
        },acceptData: function(a) {
            if (a.nodeName) {
                var b = be.noData[a.nodeName.toLowerCase()];
                if (b) {
                    return !(b === true || a.getAttribute("classid") !== b)
                }
            }
            return true
        }});
    be.fn.extend({data: function(h, f) {
            var g = null;
            if (typeof h === "undefined") {
                if (this.length) {
                    var d = this[0].attributes, b;
                    g = be.data(this[0]);
                    for (var a = 0, c = d.length; a < c; a++) {
                        b = d[a].name;
                        if (b.indexOf("data-") === 0) {
                            b = b.substr(5);
                            bB(this[0], b, g[b])
                        }
                    }
                }
                return g
            } else {
                if (typeof h === "object") {
                    return this.each(function() {
                        be.data(this, h)
                    })
                }
            }
            var e = h.split(".");
            e[1] = e[1] ? "." + e[1] : "";
            if (f === aP) {
                g = this.triggerHandler("getData" + e[1] + "!", [e[0]]);
                if (g === aP && this.length) {
                    g = be.data(this[0], h);
                    g = bB(this[0], h, g)
                }
                return g === aP && e[1] ? this.data(e[0]) : g
            } else {
                return this.each(function() {
                    var i = be(this), j = [e[0], f];
                    i.triggerHandler("setData" + e[1] + "!", j);
                    be.data(this, h, f);
                    i.triggerHandler("changeData" + e[1] + "!", j)
                })
            }
        },removeData: function(a) {
            return this.each(function() {
                be.removeData(this, a)
            })
        }});
    function bB(c, d, b) {
        if (b === aP && c.nodeType === 1) {
            b = c.getAttribute("data-" + d);
            if (typeof b === "string") {
                try {
                    b = b === "true" ? true : b === "false" ? false : b === "null" ? null : !be.isNaN(b) ? parseFloat(b) : bU.test(b) ? be.parseJSON(b) : b
                } catch (a) {
                }
                be.data(c, d, b)
            } else {
                b = aP
            }
        }
        return b
    }
    be.extend({queue: function(c, d, a) {
            if (!c) {
                return
            }
            d = (d || "fx") + "queue";
            var b = be.data(c, d);
            if (!a) {
                return b || []
            }
            if (!b || be.isArray(a)) {
                b = be.data(c, d, be.makeArray(a))
            } else {
                b.push(a)
            }
            return b
        },dequeue: function(a, b) {
            b = b || "fx";
            var d = be.queue(a, b), c = d.shift();
            if (c === "inprogress") {
                c = d.shift()
            }
            if (c) {
                if (b === "fx") {
                    d.unshift("inprogress")
                }
                c.call(a, function() {
                    be.dequeue(a, b)
                })
            }
        }});
    be.fn.extend({queue: function(b, a) {
            if (typeof b !== "string") {
                a = b;
                b = "fx"
            }
            if (a === aP) {
                return be.queue(this[0], b)
            }
            return this.each(function(c) {
                var d = be.queue(this, b, a);
                if (b === "fx" && d[0] !== "inprogress") {
                    be.dequeue(this, b)
                }
            })
        },dequeue: function(a) {
            return this.each(function() {
                be.dequeue(this, a)
            })
        },delay: function(a, b) {
            a = be.fx ? be.fx.speeds[a] || a : a;
            b = b || "fx";
            return this.queue(b, function() {
                var c = this;
                setTimeout(function() {
                    be.dequeue(c, b)
                }, a)
            })
        },clearQueue: function(a) {
            return this.queue(a || "fx", [])
        }});
    var bW = /[\n\t]/g, cc = /\s+/, bS = /\r/g, cd = /^(?:href|src|style)$/, bc = /^(?:button|input)$/i, aU = /^(?:button|input|object|select|textarea)$/i, a8 = /^a(?:rea)?$/i, by = /^(?:radio|checkbox)$/i;
    be.props = {"for": "htmlFor","class": "className",readonly: "readOnly",maxlength: "maxLength",cellspacing: "cellSpacing",rowspan: "rowSpan",colspan: "colSpan",tabindex: "tabIndex",usemap: "useMap",frameborder: "frameBorder"};
    be.fn.extend({attr: function(b, a) {
            return be.access(this, b, a, true, be.attr)
        },removeAttr: function(b, a) {
            return this.each(function() {
                be.attr(this, b, "");
                if (this.nodeType === 1) {
                    this.removeAttribute(b)
                }
            })
        },addClass: function(b) {
            if (be.isFunction(b)) {
                return this.each(function(j) {
                    var k = be(this);
                    k.addClass(b.call(this, j, k.attr("class")))
                })
            }
            if (b && typeof b === "string") {
                var i = (b || "").split(cc);
                for (var f = 0, g = this.length; f < g; f++) {
                    var h = this[f];
                    if (h.nodeType === 1) {
                        if (!h.className) {
                            h.className = b
                        } else {
                            var e = " " + h.className + " ", c = h.className;
                            for (var d = 0, a = i.length; d < a; d++) {
                                if (e.indexOf(" " + i[d] + " ") < 0) {
                                    c += " " + i[d]
                                }
                            }
                            h.className = be.trim(c)
                        }
                    }
                }
            }
            return this
        },removeClass: function(g) {
            if (be.isFunction(g)) {
                return this.each(function(i) {
                    var j = be(this);
                    j.removeClass(g.call(this, i, j.attr("class")))
                })
            }
            if ((g && typeof g === "string") || g === aP) {
                var f = (g || "").split(cc);
                for (var b = 0, c = this.length; b < c; b++) {
                    var h = this[b];
                    if (h.nodeType === 1 && h.className) {
                        if (g) {
                            var a = (" " + h.className + " ").replace(bW, " ");
                            for (var e = 0, d = f.length; e < d; e++) {
                                a = a.replace(" " + f[e] + " ", " ")
                            }
                            h.className = be.trim(a)
                        } else {
                            h.className = ""
                        }
                    }
                }
            }
            return this
        },toggleClass: function(a, c) {
            var b = typeof a, d = typeof c === "boolean";
            if (be.isFunction(a)) {
                return this.each(function(e) {
                    var f = be(this);
                    f.toggleClass(a.call(this, e, f.attr("class"), c), c)
                })
            }
            return this.each(function() {
                if (b === "string") {
                    var g, h = 0, i = be(this), f = c, e = a.split(cc);
                    while ((g = e[h++])) {
                        f = d ? f : !i.hasClass(g);
                        i[f ? "addClass" : "removeClass"](g)
                    }
                } else {
                    if (b === "undefined" || b === "boolean") {
                        if (this.className) {
                            be.data(this, "__className__", this.className)
                        }
                        this.className = this.className || a === false ? "" : be.data(this, "__className__") || ""
                    }
                }
            })
        },hasClass: function(d) {
            var a = " " + d + " ";
            for (var b = 0, c = this.length; b < c; b++) {
                if ((" " + this[b].className + " ").replace(bW, " ").indexOf(a) > -1) {
                    return true
                }
            }
            return false
        },val: function(c) {
            if (!arguments.length) {
                var i = this[0];
                if (i) {
                    if (be.nodeName(i, "option")) {
                        var j = i.attributes.value;
                        return !j || j.specified ? i.value : i.text
                    }
                    if (be.nodeName(i, "select")) {
                        var e = i.selectedIndex, b = [], a = i.options, f = i.type === "select-one";
                        if (e < 0) {
                            return null
                        }
                        for (var h = f ? e : 0, d = f ? e + 1 : a.length; h < d; h++) {
                            var g = a[h];
                            if (g.selected && (be.support.optDisabled ? !g.disabled : g.getAttribute("disabled") === null) && (!g.parentNode.disabled || !be.nodeName(g.parentNode, "optgroup"))) {
                                c = be(g).val();
                                if (f) {
                                    return c
                                }
                                b.push(c)
                            }
                        }
                        return b
                    }
                    if (by.test(i.type) && !be.support.checkOn) {
                        return i.getAttribute("value") === null ? "on" : i.value
                    }
                    return (i.value || "").replace(bS, "")
                }
                return aP
            }
            var k = be.isFunction(c);
            return this.each(function(l) {
                var m = be(this), o = c;
                if (this.nodeType !== 1) {
                    return
                }
                if (k) {
                    o = c.call(this, l, m.val())
                }
                if (o == null) {
                    o = ""
                } else {
                    if (typeof o === "number") {
                        o += ""
                    } else {
                        if (be.isArray(o)) {
                            o = be.map(o, function(p) {
                                return p == null ? "" : p + ""
                            })
                        }
                    }
                }
                if (be.isArray(o) && by.test(this.type)) {
                    this.checked = be.inArray(m.val(), o) >= 0
                } else {
                    if (be.nodeName(this, "select")) {
                        var n = be.makeArray(o);
                        be("option", this).each(function() {
                            this.selected = be.inArray(be(this).val(), n) >= 0
                        });
                        if (!n.length) {
                            this.selectedIndex = -1
                        }
                    } else {
                        this.value = o
                    }
                }
            })
        }});
    be.extend({attrFn: {val: true,css: true,html: true,text: true,data: true,width: true,height: true,offset: true},attr: function(i, j, d, a) {
            if (!i || i.nodeType === 3 || i.nodeType === 8) {
                return aP
            }
            if (a && j in be.attrFn) {
                return be(i)[j](d)
            }
            var h = i.nodeType !== 1 || !be.isXMLDoc(i), e = d !== aP;
            j = h && be.props[j] || j;
            var f = cd.test(j);
            if (j === "selected" && !be.support.optSelected) {
                var c = i.parentNode;
                if (c) {
                    c.selectedIndex;
                    if (c.parentNode) {
                        c.parentNode.selectedIndex
                    }
                }
            }
            if ((j in i || i[j] !== aP) && h && !f) {
                if (e) {
                    if (j === "type" && bc.test(i.nodeName) && i.parentNode) {
                        be.error("type property can't be changed")
                    }
                    if (d === null) {
                        if (i.nodeType === 1) {
                            i.removeAttribute(j)
                        }
                    } else {
                        i[j] = d
                    }
                }
                if (be.nodeName(i, "form") && i.getAttributeNode(j)) {
                    return i.getAttributeNode(j).nodeValue
                }
                if (j === "tabIndex") {
                    var b = i.getAttributeNode("tabIndex");
                    return b && b.specified ? b.value : aU.test(i.nodeName) || a8.test(i.nodeName) && i.href ? 0 : aP
                }
                return i[j]
            }
            if (!be.support.style && h && j === "style") {
                if (e) {
                    i.style.cssText = "" + d
                }
                return i.style.cssText
            }
            if (e) {
                i.setAttribute(j, "" + d)
            }
            if (!i.attributes[j] && (i.hasAttribute && !i.hasAttribute(j))) {
                return aP
            }
            var g = !be.support.hrefNormalized && h && f ? i.getAttribute(j, 2) : i.getAttribute(j);
            return g === null ? aP : g
        }});
    var bJ = /\.(.*)$/, ce = /^(?:textarea|input|select)$/i, bK = /\./g, bp = / /g, b0 = /[^\w\s.|`]/g, aS = function(a) {
        return a.replace(b0, "\\$&")
    }, aT = {focusin: 0,focusout: 0};
    be.event = {add: function(k, g, a, i) {
            if (k.nodeType === 3 || k.nodeType === 8) {
                return
            }
            if (be.isWindow(k) && (k !== cg && !k.frameElement)) {
                k = cg
            }
            if (a === false) {
                a = cb
            } else {
                if (!a) {
                    return
                }
            }
            var m, c;
            if (a.handler) {
                m = a;
                a = m.handler
            }
            if (!a.guid) {
                a.guid = be.guid++
            }
            var f = be.data(k);
            if (!f) {
                return
            }
            var o = k.nodeType ? "events" : "__events__", b = f[o], h = f.handle;
            if (typeof b === "function") {
                h = b.handle;
                b = b.events
            } else {
                if (!b) {
                    if (!k.nodeType) {
                        f[o] = f = function() {
                        }
                    }
                    f.events = b = {}
                }
            }
            if (!h) {
                f.handle = h = function() {
                    return typeof be !== "undefined" && !be.event.triggered ? be.event.handle.apply(h.elem, arguments) : aP
                }
            }
            h.elem = k;
            g = g.split(" ");
            var d, j = 0, n;
            while ((d = g[j++])) {
                c = m ? be.extend({}, m) : {handler: a,data: i};
                if (d.indexOf(".") > -1) {
                    n = d.split(".");
                    d = n.shift();
                    c.namespace = n.slice(0).sort().join(".")
                } else {
                    n = [];
                    c.namespace = ""
                }
                c.type = d;
                if (!c.guid) {
                    c.guid = a.guid
                }
                var l = b[d], e = be.event.special[d] || {};
                if (!l) {
                    l = b[d] = [];
                    if (!e.setup || e.setup.call(k, i, n, h) === false) {
                        if (k.addEventListener) {
                            k.addEventListener(d, h, false)
                        } else {
                            if (k.attachEvent) {
                                k.attachEvent("on" + d, h)
                            }
                        }
                    }
                }
                if (e.add) {
                    e.add.call(k, c);
                    if (!c.handler.guid) {
                        c.handler.guid = a.guid
                    }
                }
                l.push(c);
                be.event.global[d] = true
            }
            k = null
        },global: {},remove: function(o, a, i, e) {
            if (o.nodeType === 3 || o.nodeType === 8) {
                return
            }
            if (i === false) {
                i = cb
            }
            var l, f, d, r, q = 0, h, c, t, g, b, k, m, s = o.nodeType ? "events" : "__events__", p = be.data(o), j = p && p[s];
            if (!p || !j) {
                return
            }
            if (typeof j === "function") {
                p = j;
                j = j.events
            }
            if (a && a.type) {
                i = a.handler;
                a = a.type
            }
            if (!a || typeof a === "string" && a.charAt(0) === ".") {
                a = a || "";
                for (f in j) {
                    be.event.remove(o, f + a)
                }
                return
            }
            a = a.split(" ");
            while ((f = a[q++])) {
                m = f;
                k = null;
                h = f.indexOf(".") < 0;
                c = [];
                if (!h) {
                    c = f.split(".");
                    f = c.shift();
                    t = new RegExp("(^|\\.)" + be.map(c.slice(0).sort(), aS).join("\\.(?:.*\\.)?") + "(\\.|$)")
                }
                b = j[f];
                if (!b) {
                    continue
                }
                if (!i) {
                    for (r = 0; r < b.length; r++) {
                        k = b[r];
                        if (h || t.test(k.namespace)) {
                            be.event.remove(o, m, k.handler, r);
                            b.splice(r--, 1)
                        }
                    }
                    continue
                }
                g = be.event.special[f] || {};
                for (r = e || 0; r < b.length; r++) {
                    k = b[r];
                    if (i.guid === k.guid) {
                        if (h || t.test(k.namespace)) {
                            if (e == null) {
                                b.splice(r--, 1)
                            }
                            if (g.remove) {
                                g.remove.call(o, k)
                            }
                        }
                        if (e != null) {
                            break
                        }
                    }
                }
                if (b.length === 0 || e != null && b.length === 1) {
                    if (!g.teardown || g.teardown.call(o, c) === false) {
                        be.removeEvent(o, f, p.handle)
                    }
                    l = null;
                    delete j[f]
                }
            }
            if (be.isEmptyObject(j)) {
                var n = p.handle;
                if (n) {
                    n.elem = null
                }
                delete p.events;
                delete p.handle;
                if (typeof p === "function") {
                    be.removeData(o, s)
                } else {
                    if (be.isEmptyObject(p)) {
                        be.removeData(o)
                    }
                }
            }
        },trigger: function(m, h, k) {
            var d = m.type || m, i = arguments[3];
            if (!i) {
                m = typeof m === "object" ? m[be.expando] ? m : be.extend(be.Event(d), m) : be.Event(d);
                if (d.indexOf("!") >= 0) {
                    m.type = d = d.slice(0, -1);
                    m.exclusive = true
                }
                if (!k) {
                    m.stopPropagation();
                    if (be.event.global[d]) {
                        be.each(be.cache, function() {
                            if (this.events && this.events[d]) {
                                be.event.trigger(m, h, this.handle.elem)
                            }
                        })
                    }
                }
                if (!k || k.nodeType === 3 || k.nodeType === 8) {
                    return aP
                }
                m.result = aP;
                m.target = k;
                h = be.makeArray(h);
                h.unshift(m)
            }
            m.currentTarget = k;
            var g = k.nodeType ? be.data(k, "handle") : (be.data(k, "__events__") || {}).handle;
            if (g) {
                g.apply(k, h)
            }
            var b = k.parentNode || k.ownerDocument;
            try {
                if (!(k && k.nodeName && be.noData[k.nodeName.toLowerCase()])) {
                    if (k["on" + d] && k["on" + d].apply(k, h) === false) {
                        m.result = false;
                        m.preventDefault()
                    }
                }
            } catch (c) {
            }
            if (!m.isPropagationStopped() && b) {
                be.event.trigger(m, h, b, true)
            } else {
                if (!m.isDefaultPrevented()) {
                    var l, f = m.target, n = d.replace(bJ, ""), a = be.nodeName(f, "a") && n === "click", e = be.event.special[n] || {};
                    if ((!e._default || e._default.call(k, m) === false) && !a && !(f && f.nodeName && be.noData[f.nodeName.toLowerCase()])) {
                        try {
                            if (f[n]) {
                                l = f["on" + n];
                                if (l) {
                                    f["on" + n] = null
                                }
                                be.event.triggered = true;
                                f[n]()
                            }
                        } catch (j) {
                        }
                        if (l) {
                            f["on" + n] = l
                        }
                        be.event.triggered = false
                    }
                }
            }
        },handle: function(l) {
            var c, j, k, a, b, g = [], e = be.makeArray(arguments);
            l = e[0] = be.event.fix(l || cg.event);
            l.currentTarget = this;
            c = l.type.indexOf(".") < 0 && !l.exclusive;
            if (!c) {
                k = l.type.split(".");
                l.type = k.shift();
                g = k.slice(0).sort();
                a = new RegExp("(^|\\.)" + g.join("\\.(?:.*\\.)?") + "(\\.|$)")
            }
            l.namespace = l.namespace || g.join(".");
            b = be.data(this, this.nodeType ? "events" : "__events__");
            if (typeof b === "function") {
                b = b.events
            }
            j = (b || {})[l.type];
            if (b && j) {
                j = j.slice(0);
                for (var h = 0, i = j.length; h < i; h++) {
                    var d = j[h];
                    if (c || a.test(d.namespace)) {
                        l.handler = d.handler;
                        l.data = d.data;
                        l.handleObj = d;
                        var f = d.handler.apply(this, e);
                        if (f !== aP) {
                            l.result = f;
                            if (f === false) {
                                l.preventDefault();
                                l.stopPropagation()
                            }
                        }
                        if (l.isImmediatePropagationStopped()) {
                            break
                        }
                    }
                }
            }
            return l.result
        },props: "altKey attrChange attrName bubbles button cancelable charCode clientX clientY ctrlKey currentTarget data detail eventPhase fromElement handler keyCode layerX layerY metaKey newValue offsetX offsetY pageX pageY prevValue relatedNode relatedTarget screenX screenY shiftKey srcElement target toElement view wheelDelta which".split(" "),fix: function(a) {
            if (a[be.expando]) {
                return a
            }
            var c = a;
            a = be.Event(c);
            for (var b = this.props.length, e; b; ) {
                e = this.props[--b];
                a[e] = c[e]
            }
            if (!a.target) {
                a.target = a.srcElement || bg
            }
            if (a.target.nodeType === 3) {
                a.target = a.target.parentNode
            }
            if (!a.relatedTarget && a.fromElement) {
                a.relatedTarget = a.fromElement === a.target ? a.toElement : a.fromElement
            }
            if (a.pageX == null && a.clientX != null) {
                var f = bg.documentElement, d = bg.body;
                a.pageX = a.clientX + (f && f.scrollLeft || d && d.scrollLeft || 0) - (f && f.clientLeft || d && d.clientLeft || 0);
                a.pageY = a.clientY + (f && f.scrollTop || d && d.scrollTop || 0) - (f && f.clientTop || d && d.clientTop || 0)
            }
            if (a.which == null && (a.charCode != null || a.keyCode != null)) {
                a.which = a.charCode != null ? a.charCode : a.keyCode
            }
            if (!a.metaKey && a.ctrlKey) {
                a.metaKey = a.ctrlKey
            }
            if (!a.which && a.button !== aP) {
                a.which = (a.button & 1 ? 1 : (a.button & 2 ? 3 : (a.button & 4 ? 2 : 0)))
            }
            return a
        },guid: 100000000,proxy: be.proxy,special: {ready: {setup: be.bindReady,teardown: be.noop},live: {add: function(a) {
                    be.event.add(this, a5(a.origType, a.selector), be.extend({}, a, {handler: bm,guid: a.handler.guid}))
                },remove: function(a) {
                    be.event.remove(this, a5(a.origType, a.selector), a)
                }},beforeunload: {setup: function(a, b, c) {
                    if (be.isWindow(this)) {
                        this.onbeforeunload = c
                    }
                },teardown: function(a, b) {
                    if (this.onbeforeunload === b) {
                        this.onbeforeunload = null
                    }
                }}}};
    be.removeEvent = bg.removeEventListener ? function(b, c, a) {
        if (b.removeEventListener) {
            b.removeEventListener(c, a, false)
        }
    } : function(b, c, a) {
        if (b.detachEvent) {
            b.detachEvent("on" + c, a)
        }
    };
    be.Event = function(a) {
        if (!this.preventDefault) {
            return new be.Event(a)
        }
        if (a && a.type) {
            this.originalEvent = a;
            this.type = a.type
        } else {
            this.type = a
        }
        this.timeStamp = be.now();
        this[be.expando] = true
    };
    function cb() {
        return false
    }
    function bb() {
        return true
    }
    be.Event.prototype = {preventDefault: function() {
            this.isDefaultPrevented = bb;
            var a = this.originalEvent;
            if (!a) {
                return
            }
            if (a.preventDefault) {
                a.preventDefault()
            } else {
                a.returnValue = false
            }
        },stopPropagation: function() {
            this.isPropagationStopped = bb;
            var a = this.originalEvent;
            if (!a) {
                return
            }
            if (a.stopPropagation) {
                a.stopPropagation()
            }
            a.cancelBubble = true
        },stopImmediatePropagation: function() {
            this.isImmediatePropagationStopped = bb;
            this.stopPropagation()
        },isDefaultPrevented: cb,isPropagationStopped: cb,isImmediatePropagationStopped: cb};
    var bq = function(b) {
        var c = b.relatedTarget;
        try {
            while (c && c !== this) {
                c = c.parentNode
            }
            if (c !== this) {
                b.type = b.data;
                be.event.handle.apply(this, arguments)
            }
        } catch (a) {
        }
    }, bQ = function(a) {
        a.type = a.data;
        be.event.handle.apply(this, arguments)
    };
    be.each({mouseenter: "mouseover",mouseleave: "mouseout"}, function(a, b) {
        be.event.special[a] = {setup: function(c) {
                be.event.add(this, b, c && c.selector ? bQ : bq, a)
            },teardown: function(c) {
                be.event.remove(this, b, c && c.selector ? bQ : bq)
            }}
    });
    if (!be.support.submitBubbles) {
        be.event.special.submit = {setup: function(a, b) {
                if (this.nodeName.toLowerCase() !== "form") {
                    be.event.add(this, "click.specialSubmit", function(e) {
                        var c = e.target, d = c.type;
                        if ((d === "submit" || d === "image") && be(c).closest("form").length) {
                            e.liveFired = aP;
                            return bN("submit", this, arguments)
                        }
                    });
                    be.event.add(this, "keypress.specialSubmit", function(e) {
                        var c = e.target, d = c.type;
                        if ((d === "text" || d === "password") && be(c).closest("form").length && e.keyCode === 13) {
                            e.liveFired = aP;
                            return bN("submit", this, arguments)
                        }
                    })
                } else {
                    return false
                }
            },teardown: function(a) {
                be.event.remove(this, ".specialSubmit")
            }}
    }
    if (!be.support.changeBubbles) {
        var ca, a9 = function(b) {
            var c = b.type, a = b.value;
            if (c === "radio" || c === "checkbox") {
                a = b.checked
            } else {
                if (c === "select-multiple") {
                    a = b.selectedIndex > -1 ? be.map(b.options, function(d) {
                        return d.selected
                    }).join("-") : ""
                } else {
                    if (b.nodeName.toLowerCase() === "select") {
                        a = b.selectedIndex
                    }
                }
            }
            return a
        }, bs = function bs(b) {
            var d = b.target, c, a;
            if (!ce.test(d.nodeName) || d.readOnly) {
                return
            }
            c = be.data(d, "_change_data");
            a = a9(d);
            if (b.type !== "focusout" || d.type !== "radio") {
                be.data(d, "_change_data", a)
            }
            if (c === aP || a === c) {
                return
            }
            if (c != null || a) {
                b.type = "change";
                b.liveFired = aP;
                return be.event.trigger(b, arguments[1], d)
            }
        };
        be.event.special.change = {filters: {focusout: bs,beforedeactivate: bs,click: function(a) {
                    var b = a.target, c = b.type;
                    if (c === "radio" || c === "checkbox" || b.nodeName.toLowerCase() === "select") {
                        return bs.call(this, a)
                    }
                },keydown: function(a) {
                    var b = a.target, c = b.type;
                    if ((a.keyCode === 13 && b.nodeName.toLowerCase() !== "textarea") || (a.keyCode === 32 && (c === "checkbox" || c === "radio")) || c === "select-multiple") {
                        return bs.call(this, a)
                    }
                },beforeactivate: function(a) {
                    var b = a.target;
                    be.data(b, "_change_data", a9(b))
                }},setup: function(a, b) {
                if (this.type === "file") {
                    return false
                }
                for (var c in ca) {
                    be.event.add(this, c + ".specialChange", ca[c])
                }
                return ce.test(this.nodeName)
            },teardown: function(a) {
                be.event.remove(this, ".specialChange");
                return ce.test(this.nodeName)
            }};
        ca = be.event.special.change.filters;
        ca.focus = ca.beforeactivate
    }
    function bN(b, a, c) {
        c[0].type = b;
        return be.event.handle.apply(a, c)
    }
    if (bg.addEventListener) {
        be.each({focus: "focusin",blur: "focusout"}, function(a, c) {
            be.event.special[c] = {setup: function() {
                    if (aT[c]++ === 0) {
                        bg.addEventListener(a, b, true)
                    }
                },teardown: function() {
                    if (--aT[c] === 0) {
                        bg.removeEventListener(a, b, true)
                    }
                }};
            function b(d) {
                d = be.event.fix(d);
                d.type = c;
                return be.event.trigger(d, null, d.target)
            }
        })
    }
    be.each(["bind", "one"], function(a, b) {
        be.fn[b] = function(f, e, g) {
            if (typeof f === "object") {
                for (var i in f) {
                    this[b](i, e, f[i], g)
                }
                return this
            }
            if (be.isFunction(e) || e === false) {
                g = e;
                e = aP
            }
            var h = b === "one" ? be.proxy(g, function(j) {
                be(this).unbind(j, h);
                return g.apply(this, arguments)
            }) : g;
            if (f === "unload" && b !== "one") {
                this.one(f, e, g)
            } else {
                for (var c = 0, d = this.length; c < d; c++) {
                    be.event.add(this[c], f, h, e)
                }
            }
            return this
        }
    });
    be.fn.extend({unbind: function(e, a) {
            if (typeof e === "object" && !e.preventDefault) {
                for (var b in e) {
                    this.unbind(b, e[b])
                }
            } else {
                for (var c = 0, d = this.length; c < d; c++) {
                    be.event.remove(this[c], e, a)
                }
            }
            return this
        },delegate: function(d, c, a, b) {
            return this.live(c, a, b, d)
        },undelegate: function(c, b, a) {
            if (arguments.length === 0) {
                return this.unbind("live")
            } else {
                return this.die(b, null, a, c)
            }
        },trigger: function(b, a) {
            return this.each(function() {
                be.event.trigger(b, a, this)
            })
        },triggerHandler: function(c, a) {
            if (this[0]) {
                var b = be.Event(c);
                b.preventDefault();
                b.stopPropagation();
                be.event.trigger(b, a, this[0]);
                return b.result
            }
        },toggle: function(a) {
            var c = arguments, b = 1;
            while (b < c.length) {
                be.proxy(a, c[b++])
            }
            return this.click(be.proxy(a, function(d) {
                var e = (be.data(this, "lastToggle" + a.guid) || 0) % b;
                be.data(this, "lastToggle" + a.guid, e + 1);
                d.preventDefault();
                return c[e].apply(this, arguments) || false
            }))
        },hover: function(b, a) {
            return this.mouseenter(b).mouseleave(a || b)
        }});
    var bR = {focus: "focusin",blur: "focusout",mouseenter: "mouseover",mouseleave: "mouseout"};
    be.each(["live", "die"], function(a, b) {
        be.fn[b] = function(f, i, d, m) {
            var e, h = 0, g, n, p, k = m || this.selector, o = m ? this : be(this.context);
            if (typeof f === "object" && !f.preventDefault) {
                for (var c in f) {
                    o[b](c, i, f[c], k)
                }
                return this
            }
            if (be.isFunction(i)) {
                d = i;
                i = aP
            }
            f = (f || "").split(" ");
            while ((e = f[h++]) != null) {
                g = bJ.exec(e);
                n = "";
                if (g) {
                    n = g[0];
                    e = e.replace(bJ, "")
                }
                if (e === "hover") {
                    f.push("mouseenter" + n, "mouseleave" + n);
                    continue
                }
                p = e;
                if (e === "focus" || e === "blur") {
                    f.push(bR[e] + n);
                    e = e + n
                } else {
                    e = (bR[e] || e) + n
                }
                if (b === "live") {
                    for (var j = 0, l = o.length; j < l; j++) {
                        be.event.add(o[j], "live." + a5(e, k), {data: i,selector: k,handler: d,origType: e,origHandler: d,preType: p})
                    }
                } else {
                    o.unbind("live." + a5(e, k), d)
                }
            }
            return this
        }
    });
    function bm(r) {
        var c, h, l, f, k, p, a, q, b, m, d, e, n, o = [], g = [], j = be.data(this, this.nodeType ? "events" : "__events__");
        if (typeof j === "function") {
            j = j.events
        }
        if (r.liveFired === this || !j || !j.live || r.button && r.type === "click") {
            return
        }
        if (r.namespace) {
            e = new RegExp("(^|\\.)" + r.namespace.split(".").join("\\.(?:.*\\.)?") + "(\\.|$)")
        }
        r.liveFired = this;
        var i = j.live.slice(0);
        for (a = 0; a < i.length; a++) {
            k = i[a];
            if (k.origType.replace(bJ, "") === r.type) {
                g.push(k.selector)
            } else {
                i.splice(a--, 1)
            }
        }
        f = be(r.target).closest(g, r.currentTarget);
        for (q = 0, b = f.length; q < b; q++) {
            d = f[q];
            for (a = 0; a < i.length; a++) {
                k = i[a];
                if (d.selector === k.selector && (!e || e.test(k.namespace))) {
                    p = d.elem;
                    l = null;
                    if (k.preType === "mouseenter" || k.preType === "mouseleave") {
                        r.type = k.preType;
                        l = be(r.relatedTarget).closest(k.selector)[0]
                    }
                    if (!l || l !== p) {
                        o.push({elem: p,handleObj: k,level: d.level})
                    }
                }
            }
        }
        for (q = 0, b = o.length; q < b; q++) {
            f = o[q];
            if (h && f.level > h) {
                break
            }
            r.currentTarget = f.elem;
            r.data = f.handleObj.data;
            r.handleObj = f.handleObj;
            n = f.handleObj.origHandler.apply(f.elem, arguments);
            if (n === false || r.isPropagationStopped()) {
                h = f.level;
                if (n === false) {
                    c = false
                }
                if (r.isImmediatePropagationStopped()) {
                    break
                }
            }
        }
        return c
    }
    function a5(a, b) {
        return (a && a !== "*" ? a + "." : "") + b.replace(bK, "`").replace(bp, "&")
    }
    be.each(("blur focus focusin focusout load resize scroll unload click dblclick mousedown mouseup mousemove mouseover mouseout mouseenter mouseleave change select submit keydown keypress keyup error").split(" "), function(a, b) {
        be.fn[b] = function(c, d) {
            if (d == null) {
                d = c;
                c = null
            }
            return arguments.length > 0 ? this.bind(b, c, d) : this.trigger(b)
        };
        if (be.attrFn) {
            be.attrFn[b] = true
        }
    });
    if (cg.attachEvent && !cg.addEventListener) {
        be(cg).bind("unload", function() {
            for (var a in be.cache) {
                if (be.cache[a].handle) {
                    try {
                        be.event.remove(be.cache[a].handle.elem)
                    } catch (b) {
                    }
                }
            }
        })
    }
    (function() {
        var q = /((?:\((?:\([^()]+\)|[^()]+)+\)|\[(?:\[[^\[\]]*\]|['"][^'"]*['"]|[^\[\]'"]+)+\]|\\.|[^ >+~,(\[\\]+)+|[>+~])(\s*,\s*)?((?:.|\r|\n)*)/g, g = 0, l = Object.prototype.toString, a = false, h = true;
        [0, 0].sort(function() {
            h = false;
            return 0
        });
        var n = function(C, H, z, y) {
            z = z || [];
            H = H || bg;
            var w = H;
            if (H.nodeType !== 1 && H.nodeType !== 9) {
                return []
            }
            if (!C || typeof C !== "string") {
                return z
            }
            var F, u, r, G, v, s, t, A, D = true, E = n.isXML(H), B = [], x = C;
            do {
                q.exec("");
                F = q.exec(x);
                if (F) {
                    x = F[3];
                    B.push(F[1]);
                    if (F[2]) {
                        G = F[3];
                        break
                    }
                }
            } while (F);
            if (B.length > 1 && f.exec(C)) {
                if (B.length === 2 && k.relative[B[0]]) {
                    u = i(B[0] + B[1], H)
                } else {
                    u = k.relative[B[0]] ? [H] : n(B.shift(), H);
                    while (B.length) {
                        C = B.shift();
                        if (k.relative[C]) {
                            C += B.shift()
                        }
                        u = i(C, u)
                    }
                }
            } else {
                if (!y && B.length > 1 && H.nodeType === 9 && !E && k.match.ID.test(B[0]) && !k.match.ID.test(B[B.length - 1])) {
                    v = n.find(B.shift(), H, E);
                    H = v.expr ? n.filter(v.expr, v.set)[0] : v.set[0]
                }
                if (H) {
                    v = y ? {expr: B.pop(),set: o(y)} : n.find(B.pop(), B.length === 1 && (B[0] === "~" || B[0] === "+") && H.parentNode ? H.parentNode : H, E);
                    u = v.expr ? n.filter(v.expr, v.set) : v.set;
                    if (B.length > 0) {
                        r = o(u)
                    } else {
                        D = false
                    }
                    while (B.length) {
                        s = B.pop();
                        t = s;
                        if (!k.relative[s]) {
                            s = ""
                        } else {
                            t = B.pop()
                        }
                        if (t == null) {
                            t = H
                        }
                        k.relative[s](r, t, E)
                    }
                } else {
                    r = B = []
                }
            }
            if (!r) {
                r = u
            }
            if (!r) {
                n.error(s || C)
            }
            if (l.call(r) === "[object Array]") {
                if (!D) {
                    z.push.apply(z, r)
                } else {
                    if (H && H.nodeType === 1) {
                        for (A = 0; r[A] != null; A++) {
                            if (r[A] && (r[A] === true || r[A].nodeType === 1 && n.contains(H, r[A]))) {
                                z.push(u[A])
                            }
                        }
                    } else {
                        for (A = 0; r[A] != null; A++) {
                            if (r[A] && r[A].nodeType === 1) {
                                z.push(u[A])
                            }
                        }
                    }
                }
            } else {
                o(r, z)
            }
            if (G) {
                n(G, w, z, y);
                n.uniqueSort(z)
            }
            return z
        };
        n.uniqueSort = function(r) {
            if (m) {
                a = h;
                r.sort(m);
                if (a) {
                    for (var s = 1; s < r.length; s++) {
                        if (r[s] === r[s - 1]) {
                            r.splice(s--, 1)
                        }
                    }
                }
            }
            return r
        };
        n.matches = function(s, r) {
            return n(s, null, null, r)
        };
        n.matchesSelector = function(s, r) {
            return n(r, null, null, [s]).length > 0
        };
        n.find = function(s, z, r) {
            var t;
            if (!s) {
                return []
            }
            for (var w = 0, x = k.order.length; w < x; w++) {
                var v, u = k.order[w];
                if ((v = k.leftMatch[u].exec(s))) {
                    var y = v[1];
                    v.splice(1, 1);
                    if (y.substr(y.length - 1) !== "\\") {
                        v[1] = (v[1] || "").replace(/\\/g, "");
                        t = k.find[u](v, z, r);
                        if (t != null) {
                            s = s.replace(k.match[u], "");
                            break
                        }
                    }
                }
            }
            if (!t) {
                t = z.getElementsByTagName("*")
            }
            return {set: t,expr: s}
        };
        n.filter = function(w, x, t, D) {
            var B, H, F = w, r = [], z = x, A = x && x[0] && n.isXML(x[0]);
            while (w && x.length) {
                for (var y in k.filter) {
                    if ((B = k.leftMatch[y].exec(w)) != null && B[2]) {
                        var s, u, G = k.filter[y], E = B[1];
                        H = false;
                        B.splice(1, 1);
                        if (E.substr(E.length - 1) === "\\") {
                            continue
                        }
                        if (z === r) {
                            r = []
                        }
                        if (k.preFilter[y]) {
                            B = k.preFilter[y](B, z, t, r, D, A);
                            if (!B) {
                                H = s = true
                            } else {
                                if (B === true) {
                                    continue
                                }
                            }
                        }
                        if (B) {
                            for (var C = 0; (u = z[C]) != null; C++) {
                                if (u) {
                                    s = G(u, B, C, z);
                                    var v = D ^ !!s;
                                    if (t && s != null) {
                                        if (v) {
                                            H = true
                                        } else {
                                            z[C] = false
                                        }
                                    } else {
                                        if (v) {
                                            r.push(u);
                                            H = true
                                        }
                                    }
                                }
                            }
                        }
                        if (s !== aP) {
                            if (!t) {
                                z = r
                            }
                            w = w.replace(k.match[y], "");
                            if (!H) {
                                return []
                            }
                            break
                        }
                    }
                }
                if (w === F) {
                    if (H == null) {
                        n.error(w)
                    } else {
                        break
                    }
                }
                F = w
            }
            return z
        };
        n.error = function(r) {
            throw "Syntax error, unrecognized expression: " + r
        };
        var k = n.selectors = {order: ["ID", "NAME", "TAG"],match: {ID: /#((?:[\w\u00c0-\uFFFF\-]|\\.)+)/,CLASS: /\.((?:[\w\u00c0-\uFFFF\-]|\\.)+)/,NAME: /\[name=['"]*((?:[\w\u00c0-\uFFFF\-]|\\.)+)['"]*\]/,ATTR: /\[\s*((?:[\w\u00c0-\uFFFF\-]|\\.)+)\s*(?:(\S?=)\s*(['"]*)(.*?)\3|)\s*\]/,TAG: /^((?:[\w\u00c0-\uFFFF\*\-]|\\.)+)/,CHILD: /:(only|nth|last|first)-child(?:\((even|odd|[\dn+\-]*)\))?/,POS: /:(nth|eq|gt|lt|first|last|even|odd)(?:\((\d*)\))?(?=[^\-]|$)/,PSEUDO: /:((?:[\w\u00c0-\uFFFF\-]|\\.)+)(?:\((['"]?)((?:\([^\)]+\)|[^\(\)]*)+)\2\))?/},leftMatch: {},attrMap: {"class": "className","for": "htmlFor"},attrHandle: {href: function(r) {
                    return r.getAttribute("href")
                }},relative: {"+": function(w, t) {
                    var r = typeof t === "string", x = r && !/\W/.test(t), v = r && !x;
                    if (x) {
                        t = t.toLowerCase()
                    }
                    for (var s = 0, u = w.length, y; s < u; s++) {
                        if ((y = w[s])) {
                            while ((y = y.previousSibling) && y.nodeType !== 1) {
                            }
                            w[s] = v || y && y.nodeName.toLowerCase() === t ? y || false : y === t
                        }
                    }
                    if (v) {
                        n.filter(t, w, true)
                    }
                },">": function(v, t) {
                    var w, x = typeof t === "string", s = 0, u = v.length;
                    if (x && !/\W/.test(t)) {
                        t = t.toLowerCase();
                        for (; s < u; s++) {
                            w = v[s];
                            if (w) {
                                var r = w.parentNode;
                                v[s] = r.nodeName.toLowerCase() === t ? r : false
                            }
                        }
                    } else {
                        for (; s < u; s++) {
                            w = v[s];
                            if (w) {
                                v[s] = x ? w.parentNode : w.parentNode === t
                            }
                        }
                        if (x) {
                            n.filter(t, v, true)
                        }
                    }
                },"": function(r, t, v) {
                    var w, s = g++, u = p;
                    if (typeof t === "string" && !/\W/.test(t)) {
                        t = t.toLowerCase();
                        w = t;
                        u = b
                    }
                    u("parentNode", t, s, r, w, v)
                },"~": function(r, t, v) {
                    var w, s = g++, u = p;
                    if (typeof t === "string" && !/\W/.test(t)) {
                        t = t.toLowerCase();
                        w = t;
                        u = b
                    }
                    u("previousSibling", t, s, r, w, v)
                }},find: {ID: function(t, s, r) {
                    if (typeof s.getElementById !== "undefined" && !r) {
                        var u = s.getElementById(t[1]);
                        return u && u.parentNode ? [u] : []
                    }
                },NAME: function(s, v) {
                    if (typeof v.getElementsByName !== "undefined") {
                        var t = [], w = v.getElementsByName(s[1]);
                        for (var r = 0, u = w.length; r < u; r++) {
                            if (w[r].getAttribute("name") === s[1]) {
                                t.push(w[r])
                            }
                        }
                        return t.length === 0 ? null : t
                    }
                },TAG: function(s, r) {
                    return r.getElementsByTagName(s[1])
                }},preFilter: {CLASS: function(r, t, s, v, w, u) {
                    r = " " + r[1].replace(/\\/g, "") + " ";
                    if (u) {
                        return r
                    }
                    for (var y = 0, x; (x = t[y]) != null; y++) {
                        if (x) {
                            if (w ^ (x.className && (" " + x.className + " ").replace(/[\t\n]/g, " ").indexOf(r) >= 0)) {
                                if (!s) {
                                    v.push(x)
                                }
                            } else {
                                if (s) {
                                    t[y] = false
                                }
                            }
                        }
                    }
                    return false
                },ID: function(r) {
                    return r[1].replace(/\\/g, "")
                },TAG: function(r, s) {
                    return r[1].toLowerCase()
                },CHILD: function(s) {
                    if (s[1] === "nth") {
                        var r = /(-?)(\d*)n((?:\+|-)?\d*)/.exec(s[2] === "even" && "2n" || s[2] === "odd" && "2n+1" || !/\D/.test(s[2]) && "0n+" + s[2] || s[2]);
                        s[2] = (r[1] + (r[2] || 1)) - 0;
                        s[3] = r[3] - 0
                    }
                    s[0] = g++;
                    return s
                },ATTR: function(x, t, s, u, w, v) {
                    var r = x[1].replace(/\\/g, "");
                    if (!v && k.attrMap[r]) {
                        x[1] = k.attrMap[r]
                    }
                    if (x[2] === "~=") {
                        x[4] = " " + x[4] + " "
                    }
                    return x
                },PSEUDO: function(w, t, s, u, v) {
                    if (w[1] === "not") {
                        if ((q.exec(w[3]) || "").length > 1 || /^\w/.test(w[3])) {
                            w[3] = n(w[3], null, null, t)
                        } else {
                            var r = n.filter(w[3], t, s, true ^ v);
                            if (!s) {
                                u.push.apply(u, r)
                            }
                            return false
                        }
                    } else {
                        if (k.match.POS.test(w[0]) || k.match.CHILD.test(w[0])) {
                            return true
                        }
                    }
                    return w
                },POS: function(r) {
                    r.unshift(true);
                    return r
                }},filters: {enabled: function(r) {
                    return r.disabled === false && r.type !== "hidden"
                },disabled: function(r) {
                    return r.disabled === true
                },checked: function(r) {
                    return r.checked === true
                },selected: function(r) {
                    r.parentNode.selectedIndex;
                    return r.selected === true
                },parent: function(r) {
                    return !!r.firstChild
                },empty: function(r) {
                    return !r.firstChild
                },has: function(r, s, t) {
                    return !!n(t[3], r).length
                },header: function(r) {
                    return (/h\d/i).test(r.nodeName)
                },text: function(r) {
                    return "text" === r.type
                },radio: function(r) {
                    return "radio" === r.type
                },checkbox: function(r) {
                    return "checkbox" === r.type
                },file: function(r) {
                    return "file" === r.type
                },password: function(r) {
                    return "password" === r.type
                },submit: function(r) {
                    return "submit" === r.type
                },image: function(r) {
                    return "image" === r.type
                },reset: function(r) {
                    return "reset" === r.type
                },button: function(r) {
                    return "button" === r.type || r.nodeName.toLowerCase() === "button"
                },input: function(r) {
                    return (/input|select|textarea|button/i).test(r.nodeName)
                }},setFilters: {first: function(r, s) {
                    return s === 0
                },last: function(s, t, u, r) {
                    return t === r.length - 1
                },even: function(r, s) {
                    return s % 2 === 0
                },odd: function(r, s) {
                    return s % 2 === 1
                },lt: function(r, s, t) {
                    return s < t[3] - 0
                },gt: function(r, s, t) {
                    return s > t[3] - 0
                },nth: function(r, s, t) {
                    return t[3] - 0 === s
                },eq: function(r, s, t) {
                    return t[3] - 0 === s
                }},filter: {PSEUDO: function(x, s, t, r) {
                    var z = s[1], y = k.filters[z];
                    if (y) {
                        return y(x, t, s, r)
                    } else {
                        if (z === "contains") {
                            return (x.textContent || x.innerText || n.getText([x]) || "").indexOf(s[3]) >= 0
                        } else {
                            if (z === "not") {
                                var w = s[3];
                                for (var u = 0, v = w.length; u < v; u++) {
                                    if (w[u] === x) {
                                        return false
                                    }
                                }
                                return true
                            } else {
                                n.error("Syntax error, unrecognized expression: " + z)
                            }
                        }
                    }
                },CHILD: function(A, x) {
                    var u = x[1], z = A;
                    switch (u) {
                        case "only":
                        case "first":
                            while ((z = z.previousSibling)) {
                                if (z.nodeType === 1) {
                                    return false
                                }
                            }
                            if (u === "first") {
                                return true
                            }
                            z = A;
                        case "last":
                            while ((z = z.nextSibling)) {
                                if (z.nodeType === 1) {
                                    return false
                                }
                            }
                            return true;
                        case "nth":
                            var y = x[2], r = x[3];
                            if (y === 1 && r === 0) {
                                return true
                            }
                            var v = x[0], s = A.parentNode;
                            if (s && (s.sizcache !== v || !A.nodeIndex)) {
                                var w = 0;
                                for (z = s.firstChild; 
                                z; z = z.nextSibling) {
                                    if (z.nodeType === 1) {
                                        z.nodeIndex = ++w
                                    }
                                }
                                s.sizcache = v
                            }
                            var t = A.nodeIndex - r;
                            if (y === 0) {
                                return t === 0
                            } else {
                                return (t % y === 0 && t / y >= 0)
                            }
                    }
                },ID: function(r, s) {
                    return r.nodeType === 1 && r.getAttribute("id") === s
                },TAG: function(r, s) {
                    return (s === "*" && r.nodeType === 1) || r.nodeName.toLowerCase() === s
                },CLASS: function(r, s) {
                    return (" " + (r.className || r.getAttribute("class")) + " ").indexOf(s) > -1
                },ATTR: function(w, r) {
                    var s = r[1], u = k.attrHandle[s] ? k.attrHandle[s](w) : w[s] != null ? w[s] : w.getAttribute(s), v = u + "", x = r[2], t = r[4];
                    return u == null ? x === "!=" : x === "=" ? v === t : x === "*=" ? v.indexOf(t) >= 0 : x === "~=" ? (" " + v + " ").indexOf(t) >= 0 : !t ? v && u !== false : x === "!=" ? v !== t : x === "^=" ? v.indexOf(t) === 0 : x === "$=" ? v.substr(v.length - t.length) === t : x === "|=" ? v === t || v.substr(0, t.length + 1) === t + "-" : false
                },POS: function(w, t, s, v) {
                    var u = t[2], r = k.setFilters[u];
                    if (r) {
                        return r(w, s, t, v)
                    }
                }}};
        var f = k.match.POS, j = function(r, s) {
            return "\\" + (s - 0 + 1)
        };
        for (var c in k.match) {
            k.match[c] = new RegExp(k.match[c].source + (/(?![^\[]*\])(?![^\(]*\))/.source));
            k.leftMatch[c] = new RegExp(/(^(?:.|\r|\n)*?)/.source + k.match[c].source.replace(/\\(\d+)/g, j))
        }
        var o = function(r, s) {
            r = Array.prototype.slice.call(r, 0);
            if (s) {
                s.push.apply(s, r);
                return s
            }
            return r
        };
        try {
            Array.prototype.slice.call(bg.documentElement.childNodes, 0)[0].nodeType
        } catch (e) {
            o = function(v, r) {
                var s = 0, t = r || [];
                if (l.call(v) === "[object Array]") {
                    Array.prototype.push.apply(t, v)
                } else {
                    if (typeof v.length === "number") {
                        for (var u = v.length; s < u; s++) {
                            t.push(v[s])
                        }
                    } else {
                        for (; v[s]; s++) {
                            t.push(v[s])
                        }
                    }
                }
                return t
            }
        }
        var m, d;
        if (bg.documentElement.compareDocumentPosition) {
            m = function(r, s) {
                if (r === s) {
                    a = true;
                    return 0
                }
                if (!r.compareDocumentPosition || !s.compareDocumentPosition) {
                    return r.compareDocumentPosition ? -1 : 1
                }
                return r.compareDocumentPosition(s) & 4 ? -1 : 1
            }
        } else {
            m = function(s, t) {
                var v, z, y = [], A = [], w = s.parentNode, u = t.parentNode, r = w;
                if (s === t) {
                    a = true;
                    return 0
                } else {
                    if (w === u) {
                        return d(s, t)
                    } else {
                        if (!w) {
                            return -1
                        } else {
                            if (!u) {
                                return 1
                            }
                        }
                    }
                }
                while (r) {
                    y.unshift(r);
                    r = r.parentNode
                }
                r = u;
                while (r) {
                    A.unshift(r);
                    r = r.parentNode
                }
                v = y.length;
                z = A.length;
                for (var x = 0; x < v && x < z; x++) {
                    if (y[x] !== A[x]) {
                        return d(y[x], A[x])
                    }
                }
                return x === v ? d(s, A[x], -1) : d(y[x], t, 1)
            };
            d = function(t, u, s) {
                if (t === u) {
                    return s
                }
                var r = t.nextSibling;
                while (r) {
                    if (r === u) {
                        return -1
                    }
                    r = r.nextSibling
                }
                return 1
            }
        }
        n.getText = function(u) {
            var t = "", r;
            for (var s = 0; u[s]; s++) {
                r = u[s];
                if (r.nodeType === 3 || r.nodeType === 4) {
                    t += r.nodeValue
                } else {
                    if (r.nodeType !== 8) {
                        t += n.getText(r.childNodes)
                    }
                }
            }
            return t
        };
        (function() {
            var s = bg.createElement("div"), r = "script" + (new Date()).getTime(), t = bg.documentElement;
            s.innerHTML = "<a name='" + r + "'/>";
            t.insertBefore(s, t.firstChild);
            if (bg.getElementById(r)) {
                k.find.ID = function(x, w, v) {
                    if (typeof w.getElementById !== "undefined" && !v) {
                        var u = w.getElementById(x[1]);
                        return u ? u.id === x[1] || typeof u.getAttributeNode !== "undefined" && u.getAttributeNode("id").nodeValue === x[1] ? [u] : aP : []
                    }
                };
                k.filter.ID = function(v, u) {
                    var w = typeof v.getAttributeNode !== "undefined" && v.getAttributeNode("id");
                    return v.nodeType === 1 && w && w.nodeValue === u
                }
            }
            t.removeChild(s);
            t = s = null
        })();
        (function() {
            var r = bg.createElement("div");
            r.appendChild(bg.createComment(""));
            if (r.getElementsByTagName("*").length > 0) {
                k.find.TAG = function(u, v) {
                    var w = v.getElementsByTagName(u[1]);
                    if (u[1] === "*") {
                        var s = [];
                        for (var t = 0; w[t]; t++) {
                            if (w[t].nodeType === 1) {
                                s.push(w[t])
                            }
                        }
                        w = s
                    }
                    return w
                }
            }
            r.innerHTML = "<a href='#'></a>";
            if (r.firstChild && typeof r.firstChild.getAttribute !== "undefined" && r.firstChild.getAttribute("href") !== "#") {
                k.attrHandle.href = function(s) {
                    return s.getAttribute("href", 2)
                }
            }
            r = null
        })();
        if (bg.querySelectorAll) {
            (function() {
                var u = n, r = bg.createElement("div"), s = "__sizzle__";
                r.innerHTML = "<p class='TEST'></p>";
                if (r.querySelectorAll && r.querySelectorAll(".TEST").length === 0) {
                    return
                }
                n = function(y, z, C, A) {
                    z = z || bg;
                    y = y.replace(/\=\s*([^'"\]]*)\s*\]/g, "='$1']");
                    if (!A && !n.isXML(z)) {
                        if (z.nodeType === 9) {
                            try {
                                return o(z.querySelectorAll(y), C)
                            } catch (w) {
                            }
                        } else {
                            if (z.nodeType === 1 && z.nodeName.toLowerCase() !== "object") {
                                var B = z.getAttribute("id"), v = B || s;
                                if (!B) {
                                    z.setAttribute("id", v)
                                }
                                try {
                                    return o(z.querySelectorAll("#" + v + " " + y), C)
                                } catch (x) {
                                }finally {
                                    if (!B) {
                                        z.removeAttribute("id")
                                    }
                                }
                            }
                        }
                    }
                    return u(y, z, C, A)
                };
                for (var t in u) {
                    n[t] = u[t]
                }
                r = null
            })()
        }
        (function() {
            var u = bg.documentElement, s = u.matchesSelector || u.mozMatchesSelector || u.webkitMatchesSelector || u.msMatchesSelector, t = false;
            try {
                s.call(bg.documentElement, "[test!='']:sizzle")
            } catch (r) {
                t = true
            }
            if (s) {
                n.matchesSelector = function(x, v) {
                    v = v.replace(/\=\s*([^'"\]]*)\s*\]/g, "='$1']");
                    if (!n.isXML(x)) {
                        try {
                            if (t || !k.match.PSEUDO.test(v) && !/!=/.test(v)) {
                                return s.call(x, v)
                            }
                        } catch (w) {
                        }
                    }
                    return n(v, null, null, [x]).length > 0
                }
            }
        })();
        (function() {
            var r = bg.createElement("div");
            r.innerHTML = "<div class='test e'></div><div class='test'></div>";
            if (!r.getElementsByClassName || r.getElementsByClassName("e").length === 0) {
                return
            }
            r.lastChild.className = "e";
            if (r.getElementsByClassName("e").length === 1) {
                return
            }
            k.order.splice(1, 0, "CLASS");
            k.find.CLASS = function(u, t, s) {
                if (typeof t.getElementsByClassName !== "undefined" && !s) {
                    return t.getElementsByClassName(u[1])
                }
            };
            r = null
        })();
        function b(z, u, v, r, t, s) {
            for (var x = 0, y = r.length; x < y; x++) {
                var A = r[x];
                if (A) {
                    var w = false;
                    A = A[z];
                    while (A) {
                        if (A.sizcache === v) {
                            w = r[A.sizset];
                            break
                        }
                        if (A.nodeType === 1 && !s) {
                            A.sizcache = v;
                            A.sizset = x
                        }
                        if (A.nodeName.toLowerCase() === u) {
                            w = A;
                            break
                        }
                        A = A[z]
                    }
                    r[x] = w
                }
            }
        }
        function p(z, u, v, r, t, s) {
            for (var x = 0, y = r.length; x < y; x++) {
                var A = r[x];
                if (A) {
                    var w = false;
                    A = A[z];
                    while (A) {
                        if (A.sizcache === v) {
                            w = r[A.sizset];
                            break
                        }
                        if (A.nodeType === 1) {
                            if (!s) {
                                A.sizcache = v;
                                A.sizset = x
                            }
                            if (typeof u !== "string") {
                                if (A === u) {
                                    w = true;
                                    break
                                }
                            } else {
                                if (n.filter(u, [A]).length > 0) {
                                    w = A;
                                    break
                                }
                            }
                        }
                        A = A[z]
                    }
                    r[x] = w
                }
            }
        }
        if (bg.documentElement.contains) {
            n.contains = function(r, s) {
                return r !== s && (r.contains ? r.contains(s) : true)
            }
        } else {
            if (bg.documentElement.compareDocumentPosition) {
                n.contains = function(r, s) {
                    return !!(r.compareDocumentPosition(s) & 16)
                }
            } else {
                n.contains = function() {
                    return false
                }
            }
        }
        n.isXML = function(s) {
            var r = (s ? s.ownerDocument || s : 0).documentElement;
            return r ? r.nodeName !== "HTML" : false
        };
        var i = function(v, u) {
            var x, r = [], y = "", s = u.nodeType ? [u] : u;
            while ((x = k.match.PSEUDO.exec(v))) {
                y += x[0];
                v = v.replace(k.match.PSEUDO, "")
            }
            v = k.relative[v] ? v + "*" : v;
            for (var w = 0, t = s.length; w < t; w++) {
                n(v, s[w], r)
            }
            return n.filter(y, r)
        };
        be.find = n;
        be.expr = n.selectors;
        be.expr[":"] = be.expr.filters;
        be.unique = n.uniqueSort;
        be.text = n.getText;
        be.isXMLDoc = n.isXML;
        be.contains = n.contains
    })();
    var bt = /Until$/, bj = /^(?:parents|prevUntil|prevAll)/, ci = /,/, b6 = /^.[^:#\[\.,]*$/, bH = Array.prototype.slice, aR = be.expr.match.POS;
    be.fn.extend({find: function(d) {
            var b = this.pushStack("", "find", d), f = 0;
            for (var a = 0, c = this.length; a < c; a++) {
                f = b.length;
                be.find(d, this[a], b);
                if (a > 0) {
                    for (var e = f; e < b.length; e++) {
                        for (var g = 0; g < f; g++) {
                            if (b[g] === b[e]) {
                                b.splice(e--, 1);
                                break
                            }
                        }
                    }
                }
            }
            return b
        },has: function(a) {
            var b = be(a);
            return this.filter(function() {
                for (var c = 0, d = b.length; c < d; c++) {
                    if (be.contains(this, b[c])) {
                        return true
                    }
                }
            })
        },not: function(a) {
            return this.pushStack(b1(this, a, false), "not", a)
        },filter: function(a) {
            return this.pushStack(b1(this, a, true), "filter", a)
        },is: function(a) {
            return !!a && be.filter(a, this).length > 0
        },closest: function(a, j) {
            var d = [], g, i, b = this[0];
            if (be.isArray(a)) {
                var e, h, f = {}, k = 1;
                if (b && a.length) {
                    for (g = 0, i = a.length; g < i; g++) {
                        h = a[g];
                        if (!f[h]) {
                            f[h] = be.expr.match.POS.test(h) ? be(h, j || this.context) : h
                        }
                    }
                    while (b && b.ownerDocument && b !== j) {
                        for (h in f) {
                            e = f[h];
                            if (e.jquery ? e.index(b) > -1 : be(b).is(e)) {
                                d.push({selector: h,elem: b,level: k})
                            }
                        }
                        b = b.parentNode;
                        k++
                    }
                }
                return d
            }
            var c = aR.test(a) ? be(a, j || this.context) : null;
            for (g = 0, i = this.length; g < i; g++) {
                b = this[g];
                while (b) {
                    if (c ? c.index(b) > -1 : be.find.matchesSelector(b, a)) {
                        d.push(b);
                        break
                    } else {
                        b = b.parentNode;
                        if (!b || !b.ownerDocument || b === j) {
                            break
                        }
                    }
                }
            }
            d = d.length > 1 ? be.unique(d) : d;
            return this.pushStack(d, "closest", a)
        },index: function(a) {
            if (!a || typeof a === "string") {
                return be.inArray(this[0], a ? be(a) : this.parent().children())
            }
            return be.inArray(a.jquery ? a[0] : a, this)
        },add: function(d, c) {
            var a = typeof d === "string" ? be(d, c || this.context) : be.makeArray(d), b = be.merge(this.get(), a);
            return this.pushStack(aV(a[0]) || aV(b[0]) ? b : be.unique(b))
        },andSelf: function() {
            return this.add(this.prevObject)
        }});
    function aV(a) {
        return !a || !a.parentNode || a.parentNode.nodeType === 11
    }
    be.each({parent: function(a) {
            var b = a.parentNode;
            return b && b.nodeType !== 11 ? b : null
        },parents: function(a) {
            return be.dir(a, "parentNode")
        },parentsUntil: function(b, c, a) {
            return be.dir(b, "parentNode", a)
        },next: function(a) {
            return be.nth(a, 2, "nextSibling")
        },prev: function(a) {
            return be.nth(a, 2, "previousSibling")
        },nextAll: function(a) {
            return be.dir(a, "nextSibling")
        },prevAll: function(a) {
            return be.dir(a, "previousSibling")
        },nextUntil: function(b, c, a) {
            return be.dir(b, "nextSibling", a)
        },prevUntil: function(b, c, a) {
            return be.dir(b, "previousSibling", a)
        },siblings: function(a) {
            return be.sibling(a.parentNode.firstChild, a)
        },children: function(a) {
            return be.sibling(a.firstChild)
        },contents: function(a) {
            return be.nodeName(a, "iframe") ? a.contentDocument || a.contentWindow.document : be.makeArray(a.childNodes)
        }}, function(b, a) {
        be.fn[b] = function(e, d) {
            var c = be.map(this, a, e);
            if (!bt.test(b)) {
                d = e
            }
            if (d && typeof d === "string") {
                c = be.filter(d, c)
            }
            c = this.length > 1 ? be.unique(c) : c;
            if ((this.length > 1 || ci.test(d)) && bj.test(b)) {
                c = c.reverse()
            }
            return this.pushStack(c, b, bH.call(arguments).join(","))
        }
    });
    be.extend({filter: function(a, c, b) {
            if (b) {
                a = ":not(" + a + ")"
            }
            return c.length === 1 ? be.find.matchesSelector(c[0], a) ? [c[0]] : [] : be.find.matches(a, c)
        },dir: function(b, c, e) {
            var d = [], a = b[c];
            while (a && a.nodeType !== 9 && (e === aP || a.nodeType !== 1 || !be(a).is(e))) {
                if (a.nodeType === 1) {
                    d.push(a)
                }
                a = a[c]
            }
            return d
        },nth: function(e, d, b, a) {
            d = d || 1;
            var c = 0;
            for (; e; e = e[b]) {
                if (e.nodeType === 1 && ++c === d) {
                    break
                }
            }
            return e
        },sibling: function(a, b) {
            var c = [];
            for (; a; a = a.nextSibling) {
                if (a.nodeType === 1 && a !== b) {
                    c.push(a)
                }
            }
            return c
        }});
    function b1(a, b, d) {
        if (be.isFunction(b)) {
            return be.grep(a, function(f, g) {
                var e = !!b.call(f, g, f);
                return e === d
            })
        } else {
            if (b.nodeType) {
                return be.grep(a, function(e, f) {
                    return (e === b) === d
                })
            } else {
                if (typeof b === "string") {
                    var c = be.grep(a, function(e) {
                        return e.nodeType === 1
                    });
                    if (b6.test(b)) {
                        return be.filter(b, c, !d)
                    } else {
                        b = be.filter(b, c)
                    }
                }
            }
        }
        return be.grep(a, function(e, f) {
            return (be.inArray(e, b) >= 0) === d
        })
    }
    var bo = / jQuery\d+="(?:\d+|null)"/g, bi = /^\s+/, bA = /<(?!area|br|col|embed|hr|img|input|link|meta|param)(([\w:]+)[^>]*)\/>/ig, bd = /<([\w:]+)/, aZ = /<tbody/i, bv = /<|&#?\w+;/, bI = /<(?:script|object|embed|option|style)/i, a6 = /checked\s*(?:[^=]|=\s*.checked.)/i, bD = /\=([^="'>\s]+\/)>/g, bf = {option: [1, "<select multiple='multiple'>", "</select>"],legend: [1, "<fieldset>", "</fieldset>"],thead: [1, "<table>", "</table>"],tr: [2, "<table><tbody>", "</tbody></table>"],td: [3, "<table><tbody><tr>", "</tr></tbody></table>"],col: [2, "<table><tbody></tbody><colgroup>", "</colgroup></table>"],area: [1, "<map>", "</map>"],_default: [0, "", ""]};
    bf.optgroup = bf.option;
    bf.tbody = bf.tfoot = bf.colgroup = bf.caption = bf.thead;
    bf.th = bf.td;
    ilog('be.support:'+JSON.stringify(be.support));
    ilog('be.support.htmlSerialize:'+be.support.htmlSerialize);
    if (!be.support.htmlSerialize) {
        bf._default = [1, "div<div>", "</div>"]
    }
    be.fn.extend({text: function(a) {
            if (be.isFunction(a)) {
                return this.each(function(b) {
                    var c = be(this);
                    c.text(a.call(this, b, c.text()))
                })
            }
            if (typeof a !== "object" && a !== aP) {
                return this.empty().append((this[0] && this[0].ownerDocument || bg).createTextNode(a))
            }
            return be.text(this)
        },wrapAll: function(b) {
            if (be.isFunction(b)) {
                return this.each(function(c) {
                    be(this).wrapAll(b.call(this, c))
                })
            }
            if (this[0]) {
                var a = be(b, this[0].ownerDocument).eq(0).clone(true);
                if (this[0].parentNode) {
                    a.insertBefore(this[0])
                }
                a.map(function() {
                    var c = this;
                    while (c.firstChild && c.firstChild.nodeType === 1) {
                        c = c.firstChild
                    }
                    return c
                }).append(this)
            }
            return this
        },wrapInner: function(a) {
            if (be.isFunction(a)) {
                return this.each(function(b) {
                    be(this).wrapInner(a.call(this, b))
                })
            }
            return this.each(function() {
                var c = be(this), b = c.contents();
                if (b.length) {
                    b.wrapAll(a)
                } else {
                    c.append(a)
                }
            })
        },wrap: function(a) {
            return this.each(function() {
                be(this).wrapAll(a)
            })
        },unwrap: function() {
            return this.parent().each(function() {
                if (!be.nodeName(this, "body")) {
                    be(this).replaceWith(this.childNodes)
                }
            }).end()
        },append: function() {
            return this.domManip(arguments, true, function(a) {
                if (this.nodeType === 1) {
                    this.appendChild(a)
                }
            })
        },prepend: function() {
            return this.domManip(arguments, true, function(a) {
                if (this.nodeType === 1) {
                    this.insertBefore(a, this.firstChild)
                }
            })
        },before: function() {
            if (this[0] && this[0].parentNode) {
                return this.domManip(arguments, false, function(b) {
                    this.parentNode.insertBefore(b, this)
                })
            } else {
                if (arguments.length) {
                    var a = be(arguments[0]);
                    a.push.apply(a, this.toArray());
                    return this.pushStack(a, "before", arguments)
                }
            }
        },after: function() {
            if (this[0] && this[0].parentNode) {
                return this.domManip(arguments, false, function(b) {
                    this.parentNode.insertBefore(b, this.nextSibling)
                })
            } else {
                if (arguments.length) {
                    var a = this.pushStack(this, "after", arguments);
                    a.push.apply(a, be(arguments[0]).toArray());
                    return a
                }
            }
        },remove: function(d, a) {
            for (var c = 0, b; (b = this[c]) != null; c++) {
                if (!d || be.filter(d, [b]).length) {
                    if (!a && b.nodeType === 1) {
                        be.cleanData(b.getElementsByTagName("*"));
                        be.cleanData([b])
                    }
                    if (b.parentNode) {
                        b.parentNode.removeChild(b)
                    }
                }
            }
            return this
        },empty: function() {
            for (var b = 0, a; (a = this[b]) != null; b++) {
                if (a.nodeType === 1) {
                    be.cleanData(a.getElementsByTagName("*"))
                }
                while (a.firstChild) {
                    a.removeChild(a.firstChild)
                }
            }
            return this
        },clone: function(a) {
            var b = this.map(function() {
                if (!be.support.noCloneEvent && !be.isXMLDoc(this)) {
                    var c = this.outerHTML, d = this.ownerDocument;
                    if (!c) {
                        var e = d.createElement("div");
                        e.appendChild(this.cloneNode(true));
                        c = e.innerHTML
                    }
                    return be.clean([c.replace(bo, "").replace(bD, '="$1">').replace(bi, "")], d)[0]
                } else {
                    return this.cloneNode(true)
                }
            });
            if (a === true) {
                a2(this, b);
                a2(this.find("*"), b.find("*"))
            }
            return b
        },html: function(b) {
            if (b === aP) {
                return this[0] && this[0].nodeType === 1 ? this[0].innerHTML.replace(bo, "") : null
            } else {
                if (typeof b === "string" && !bI.test(b) && (be.support.leadingWhitespace || !bi.test(b)) && !bf[(bd.exec(b) || ["", ""])[1].toLowerCase()]) {
                    b = b.replace(bA, "<$1></$2>");
                    try {
                        for (var c = 0, d = this.length; c < d; c++) {
                            if (this[c].nodeType === 1) {
                                be.cleanData(this[c].getElementsByTagName("*"));
                                this[c].innerHTML = b
                            }
                        }
                    } catch (a) {
                        this.empty().append(b)
                    }
                } else {
                    if (be.isFunction(b)) {
                        this.each(function(e) {
                            var f = be(this);
                            f.html(b.call(this, e, f.html()))
                        })
                    } else {
                        this.empty().append(b)
                    }
                }
            }
            return this
        },replaceWith: function(a) {
            if (this[0] && this[0].parentNode) {
                if (be.isFunction(a)) {
                    return this.each(function(b) {
                        var c = be(this), d = c.html();
                        c.replaceWith(a.call(this, b, d))
                    })
                }
                if (typeof a !== "string") {
                    a = be(a).detach()
                }
                return this.each(function() {
                    var b = this.nextSibling, c = this.parentNode;
                    be(this).remove();
                    if (b) {
                        be(b).before(a)
                    } else {
                        be(c).append(a)
                    }
                })
            } else {
                return this.pushStack(be(be.isFunction(a) ? a() : a), "replaceWith", a)
            }
        },detach: function(a) {
            return this.remove(a, true)
        },domManip: function(e, a, b) {
            var h, g, f, c, d = e[0], j = [];
            if (!be.support.checkClone && arguments.length === 3 && typeof d === "string" && a6.test(d)) {
                return this.each(function() {
                    be(this).domManip(e, a, b, true)
                })
            }
            if (be.isFunction(d)) {
                return this.each(function(l) {
                    var m = be(this);
                    e[0] = d.call(this, l, a ? m.html() : aP);
                    m.domManip(e, a, b)
                })
            }
            if (this[0]) {
                c = d && d.parentNode;
                if (be.support.parentNode && c && c.nodeType === 11 && c.childNodes.length === this.length) {
                    h = {fragment: c}
                } else {
                    h = be.buildFragment(e, this, j)
                }
                f = h.fragment;
                if (f.childNodes.length === 1) {
                    g = f = f.firstChild
                } else {
                    g = f.firstChild
                }
                if (g) {
                    a = a && be.nodeName(g, "tr");
                    for (var i = 0, k = this.length; i < k; i++) {
                        b.call(a ? ch(this[i], g) : this[i], i > 0 || h.cacheable || this.length > 1 ? f.cloneNode(true) : f)
                    }
                }
                if (j.length) {
                    be.each(j, b7)
                }
            }
            return this
        }});
    function ch(b, a) {
        return be.nodeName(b, "table") ? (b.getElementsByTagName("tbody")[0] || b.appendChild(b.ownerDocument.createElement("tbody"))) : b
    }
    function a2(a, c) {
        var b = 0;
        c.each(function() {
            if (this.nodeName !== (a[b] && a[b].nodeName)) {
                return
            }
            var e = be.data(a[b++]), f = be.data(this, e), d = e && e.events;
            if (d) {
                delete f.handle;
                f.events = {};
                for (var g in d) {
                    for (var h in d[g]) {
                        be.event.add(this, g, d[g][h], d[g][h].data)
                    }
                }
            }
        })
    }
    be.buildFragment = function(f, a, c) {
        var g, d, b, e = (a && a[0] ? a[0].ownerDocument || a[0] : bg);
        if (f.length === 1 && typeof f[0] === "string" && f[0].length < 512 && e === bg && !bI.test(f[0]) && (be.support.checkClone || !a6.test(f[0]))) {
            d = true;
            b = be.fragments[f[0]];
            if (b) {
                if (b !== 1) {
                    g = b
                }
            }
        }
        if (!g) {
            g = e.createDocumentFragment();
            be.clean(f, e, g, c)
        }
        if (d) {
            be.fragments[f[0]] = b ? g : 1
        }
        return {fragment: g,cacheable: d}
    };
    be.fragments = {};
    be.each({appendTo: "append",prependTo: "prepend",insertBefore: "before",insertAfter: "after",replaceAll: "replaceWith"}, function(b, a) {
        be.fn[b] = function(d) {
            var h = [], e = be(d), f = this.length === 1 && this[0].parentNode;
            if (f && f.nodeType === 11 && f.childNodes.length === 1 && e.length === 1) {
                e[a](this[0]);
                return this
            } else {
                for (var g = 0, c = e.length; g < c; g++) {
                    var i = (g > 0 ? this.clone(true) : this).get();
                    be(e[g])[a](i);
                    h = h.concat(i)
                }
                return this.pushStack(h, b, e.selector)
            }
        }
    });
    be.extend({clean: function(l, j, c, h) {
            j = j || bg;
            if (typeof j.createElement === "undefined") {
                j = j.ownerDocument || j[0] && j[0].ownerDocument || bg
            }
            var b = [];
            for (var d = 0, i; (i = l[d]) != null; d++) {
            	ilog('i:'+i);
            	ilog('bv:'+bv);
                if (typeof i === "number") {
                    i += ""
                }
                if (!i) {
                    continue
                }
                if (typeof i === "string" && !bv.test(i)) {
                    i = j.createTextNode(i)
                } else {
                    if (typeof i === "string") {
                        i = i.replace(bA, "<$1></$2>");
                        ilog('bf:'+JSON.stringify(bf[a]));
                        ilog('bf._default:'+JSON.stringify(bf._default));
                        var a = (bd.exec(i) || ["", ""])[1].toLowerCase(), k = bf[a] || bf._default, e = k[0], m = j.createElement("div");
                        m.innerHTML = k[1] + i + k[2];
                        ilog('m.innerHTML'+m.innerHTML);
                        ilog('e'+e);
                        while (e--) {
                        	ilog('m.lastChild:'+m.lastChild);
                            m = m.lastChild
                        }
                        if (!be.support.tbody) {
                            var n = aZ.test(i), f = a === "table" && !n ? m.firstChild && m.firstChild.childNodes : k[1] === "<table>" && !n ? m.childNodes : [];
                            for (var g = f.length - 1; g >= 0; --g) {
                                if (be.nodeName(f[g], "tbody") && !f[g].childNodes.length) {
                                    f[g].parentNode.removeChild(f[g])
                                }
                            }
                        }
                        if (!be.support.leadingWhitespace && bi.test(i)) {
                            m.insertBefore(j.createTextNode(bi.exec(i)[0]), m.firstChild)
                        }
                        i = m.childNodes
                    }
                }
                if (i.nodeType) {
                    b.push(i)
                } else {
                    b = be.merge(b, i)
                }
            }
            if (c) {
                for (d = 0; b[d]; d++) {
                    if (h && be.nodeName(b[d], "script") && (!b[d].type || b[d].type.toLowerCase() === "text/javascript")) {
                        h.push(b[d].parentNode ? b[d].parentNode.removeChild(b[d]) : b[d])
                    } else {
                        if (b[d].nodeType === 1) {
                            b.splice.apply(b, [d + 1, 0].concat(be.makeArray(b[d].getElementsByTagName("script"))))
                        }
                        c.appendChild(b[d])
                    }
                }
            }
            return b
        },cleanData: function(h) {
            var e, g, i = be.cache, b = be.event.special, c = be.support.deleteExpando;
            for (var d = 0, f; (f = h[d]) != null; d++) {
                if (f.nodeName && be.noData[f.nodeName.toLowerCase()]) {
                    continue
                }
                g = f[be.expando];
                if (g) {
                    e = i[g];
                    if (e && e.events) {
                        for (var a in e.events) {
                            if (b[a]) {
                                be.event.remove(f, a)
                            } else {
                                be.removeEvent(f, a, e.handle)
                            }
                        }
                    }
                    if (c) {
                        delete f[be.expando]
                    } else {
                        if (f.removeAttribute) {
                            f.removeAttribute(be.expando)
                        }
                    }
                    delete i[g]
                }
            }
        }});
    function b7(b, a) {
        if (a.src) {
            be.ajax({url: a.src,async: false,dataType: "script"})
        } else {
            be.globalEval(a.text || a.textContent || a.innerHTML || "")
        }
        if (a.parentNode) {
            a.parentNode.removeChild(a)
        }
    }
    var bn = /alpha\([^)]*\)/i, bh = /opacity=([^)]*)/, bO = /-([a-z])/ig, aX = /([A-Z])/g, cf = /^-?\d+(?:px)?$/i, b8 = /^-?\d/, bx = {position: "absolute",visibility: "hidden",display: "block"}, bl = ["Left", "Right"], bF = ["Top", "Bottom"], bu, bY, bP, a7 = function(b, a) {
        return a.toUpperCase()
    };
    be.fn.css = function(b, a) {
        if (arguments.length === 2 && a === aP) {
            return this
        }
        return be.access(this, b, a, true, function(c, d, e) {
            return e !== aP ? be.style(c, d, e) : be.css(c, d)
        })
    };
    be.extend({cssHooks: {opacity: {get: function(a, b) {
                    if (b) {
                        var c = bu(a, "opacity", "opacity");
                        return c === "" ? "1" : c
                    } else {
                        return a.style.opacity
                    }
                }}},cssNumber: {zIndex: true,fontWeight: true,opacity: true,zoom: true,lineHeight: true},cssProps: {"float": be.support.cssFloat ? "cssFloat" : "styleFloat"},style: function(g, h, b, f) {
            if (!g || g.nodeType === 3 || g.nodeType === 8 || !g.style) {
                return
            }
            var c, e = be.camelCase(h), i = g.style, a = be.cssHooks[e];
            h = be.cssProps[e] || e;
            if (b !== aP) {
                if (typeof b === "number" && isNaN(b) || b == null) {
                    return
                }
                if (typeof b === "number" && !be.cssNumber[e]) {
                    b += "px"
                }
                if (!a || !("set" in a) || (b = a.set(g, b)) !== aP) {
                    try {
                        i[h] = b
                    } catch (d) {
                    }
                }
            } else {
                if (a && "get" in a && (c = a.get(g, false, f)) !== aP) {
                    return c
                }
                return i[h]
            }
        },css: function(e, f, c) {
            var a, b = be.camelCase(f), d = be.cssHooks[b];
            f = be.cssProps[b] || b;
            if (d && "get" in d && (a = d.get(e, true, c)) !== aP) {
                return a
            } else {
                if (bu) {
                    return bu(e, f, b)
                }
            }
        },swap: function(a, b, e) {
            var d = {};
            for (var c in b) {
                d[c] = a.style[c];
                a.style[c] = b[c]
            }
            e.call(a);
            for (c in b) {
                a.style[c] = d[c]
            }
        },camelCase: function(a) {
            return a.replace(bO, a7)
        }});
    be.curCSS = be.css;
    be.each(["height", "width"], function(a, b) {
        be.cssHooks[b] = {get: function(f, c, d) {
                var e;
                if (c) {
                    if (f.offsetWidth !== 0) {
                        e = a4(f, b, d)
                    } else {
                        be.swap(f, bx, function() {
                            e = a4(f, b, d)
                        })
                    }
                    if (e <= 0) {
                        e = bu(f, b, b);
                        if (e === "0px" && bP) {
                            e = bP(f, b, b)
                        }
                        if (e != null) {
                            return e === "" || e === "auto" ? "0px" : e
                        }
                    }
                    if (e < 0 || e == null) {
                        e = f.style[b];
                        return e === "" || e === "auto" ? "0px" : e
                    }
                    return typeof e === "string" ? e : e + "px"
                }
            },set: function(d, c) {
                if (cf.test(c)) {
                    c = parseFloat(c);
                    if (c >= 0) {
                        return c + "px"
                    }
                } else {
                    return c
                }
            }}
    });
    if (!be.support.opacity) {
        be.cssHooks.opacity = {get: function(a, b) {
                return bh.test((b && a.currentStyle ? a.currentStyle.filter : a.style.filter) || "") ? (parseFloat(RegExp.$1) / 100) + "" : b ? "1" : ""
            },set: function(a, e) {
                var b = a.style;
                b.zoom = 1;
                var d = be.isNaN(e) ? "" : "alpha(opacity=" + e * 100 + ")", c = b.filter || "";
                b.filter = bn.test(c) ? c.replace(bn, d) : b.filter + " " + d
            }}
    }
    if (bg.defaultView && bg.defaultView.getComputedStyle) {
        bY = function(e, d, a) {
            var b, f, c;
            a = a.replace(aX, "-$1").toLowerCase();
            if (!(f = e.ownerDocument.defaultView)) {
                return aP
            }
            if ((c = f.getComputedStyle(e, null))) {
                b = c.getPropertyValue(a);
                if (b === "" && !be.contains(e.ownerDocument.documentElement, e)) {
                    b = be.style(e, a)
                }
            }
            return b
        }
    }
    if (bg.documentElement.currentStyle) {
        bP = function(f, b) {
            var e, d, c = f.currentStyle && f.currentStyle[b], a = f.style;
            if (!cf.test(c) && b8.test(c)) {
                e = a.left;
                d = f.runtimeStyle.left;
                f.runtimeStyle.left = f.currentStyle.left;
                a.left = b === "fontSize" ? "1em" : (c || 0);
                c = a.pixelLeft + "px";
                a.left = e;
                f.runtimeStyle.left = d
            }
            return c === "" ? "auto" : c
        }
    }
    bu = bY || bP;
    function a4(b, c, d) {
        var e = c === "width" ? bl : bF, a = c === "width" ? b.offsetWidth : b.offsetHeight;
        if (d === "border") {
            return a
        }
        be.each(e, function() {
            if (!d) {
                a -= parseFloat(be.css(b, "padding" + this)) || 0
            }
            if (d === "margin") {
                a += parseFloat(be.css(b, "margin" + this)) || 0
            } else {
                a -= parseFloat(be.css(b, "border" + this + "Width")) || 0
            }
        });
        return a
    }
    if (be.expr && be.expr.filters) {
        be.expr.filters.hidden = function(a) {
            var b = a.offsetWidth, c = a.offsetHeight;
            return (b === 0 && c === 0) || (!be.support.reliableHiddenOffsets && (a.style.display || be.css(a, "display")) === "none")
        };
        be.expr.filters.visible = function(a) {
            return !be.expr.filters.hidden(a)
        }
    }
    var b2 = be.now(), bz = /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, a3 = /^(?:select|textarea)/i, bM = /^(?:color|date|datetime|email|hidden|month|number|password|range|search|tel|text|time|url|week)$/i, bV = /^(?:GET|HEAD)$/, bk = /\[\]$/, a1 = /\=\?(&|$)/, bL = /\?/, b4 = /([?&])_=[^&]*/, aQ = /^(\w+:)?\/\/([^\/?#]+)/, ba = /%20/g, b5 = /#.*$/, aW = be.fn.load;
    be.fn.extend({load: function(b, f, e) {
            if (typeof b !== "string" && aW) {
                return aW.apply(this, arguments)
            } else {
                if (!this.length) {
                    return this
                }
            }
            var g = b.indexOf(" ");
            if (g >= 0) {
                var d = b.slice(g, b.length);
                b = b.slice(0, g)
            }
            var a = "GET";
            if (f) {
                if (be.isFunction(f)) {
                    e = f;
                    f = null
                } else {
                    if (typeof f === "object") {
                        f = be.param(f, be.ajaxSettings.traditional);
                        a = "POST"
                    }
                }
            }
            var c = this;
            be.ajax({url: b,type: a,dataType: "html",data: f,complete: function(h, i) {
                    if (i === "success" || i === "notmodified") {
                        c.html(d ? be("<div>").append(h.responseText.replace(bz, "")).find(d) : h.responseText)
                    }
                    if (e) {
                        c.each(e, [h.responseText, i, h])
                    }
                }});
            return this
        },serialize: function() {
            return be.param(this.serializeArray())
        },serializeArray: function() {
            return this.map(function() {
                return this.elements ? be.makeArray(this.elements) : this
            }).filter(function() {
                return this.name && !this.disabled && (this.checked || a3.test(this.nodeName) || bM.test(this.type))
            }).map(function(c, b) {
                var a = be(this).val();
                return a == null ? null : be.isArray(a) ? be.map(a, function(e, d) {
                    return {name: b.name,value: e}
                }) : {name: b.name,value: a}
            }).get()
        }});
    be.each("ajaxStart ajaxStop ajaxComplete ajaxError ajaxSuccess ajaxSend".split(" "), function(b, a) {
        be.fn[a] = function(c) {
            return this.bind(a, c)
        }
    });
    be.extend({get: function(d, b, a, c) {
            if (be.isFunction(b)) {
                c = c || a;
                a = b;
                b = null
            }
            return be.ajax({type: "GET",url: d,data: b,success: a,dataType: c})
        },getScript: function(b, a) {
            return be.get(b, null, a, "script")
        },getJSON: function(c, b, a) {
            return be.get(c, b, a, "json")
        },post: function(d, b, a, c) {
            if (be.isFunction(b)) {
                c = c || a;
                a = b;
                b = {}
            }
            return be.ajax({type: "POST",url: d,data: b,success: a,dataType: c})
        },ajaxSetup: function(a) {
            be.extend(be.ajaxSettings, a)
        },ajaxSettings: {
        	url: location.href,global: true,type: "GET",contentType: "application/x-www-form-urlencoded",processData: true,async: true,xhr: function() {
        		ilog('cg:'+cg);
                return new cg.XMLHttpRequest()
            },accepts: {xml: "application/xml, text/xml",html: "text/html",script: "text/javascript, application/javascript",json: "application/json, text/javascript",text: "text/plain",_default: "*/*"}},ajax: function(p) {
            var a = be.extend(true, {}, be.ajaxSettings, p), k, q, m, i = a.type.toUpperCase(), t = bV.test(i);
            a.url = a.url.replace(b5, "");
            a.context = p && p.context != null ? p.context : a;
            if (a.data && a.processData && typeof a.data !== "string") {
                a.data = be.param(a.data, a.traditional)
            }
            if (a.dataType === "jsonp") {
                if (i === "GET") {
                    if (!a1.test(a.url)) {
                        a.url += (bL.test(a.url) ? "&" : "?") + (a.jsonp || "callback") + "=?"
                    }
                } else {
                    if (!a.data || !a1.test(a.data)) {
                        a.data = (a.data ? a.data + "&" : "") + (a.jsonp || "callback") + "=?"
                    }
                }
                a.dataType = "json"
            }
            if (a.dataType === "json" && (a.data && a1.test(a.data) || a1.test(a.url))) {
                k = a.jsonpCallback || ("jsonp" + b2++);
                if (a.data) {
                    a.data = (a.data + "").replace(a1, "=" + k + "$1")
                }
                a.url = a.url.replace(a1, "=" + k + "$1");
                a.dataType = "script";
                var s = cg[k];
                cg[k] = function(w) {
                    if (be.isFunction(s)) {
                        s(w)
                    } else {
                        cg[k] = aP;
                        try {
                            delete cg[k]
                        } catch (x) {
                        }
                    }
                    m = w;
                    be.handleSuccess(a, e, q, m);
                    be.handleComplete(a, e, q, m);
                    if (h) {
                        h.removeChild(o)
                    }
                }
            }
            if (a.dataType === "script" && a.cache === null) {
                a.cache = false
            }
            if (a.cache === false && t) {
                var l = be.now();
                var n = a.url.replace(b4, "$1_=" + l);
                a.url = n + ((n === a.url) ? (bL.test(a.url) ? "&" : "?") + "_=" + l : "")
            }
            if (a.data && t) {
                a.url += (bL.test(a.url) ? "&" : "?") + a.data
            }
            if (a.global && be.active++ === 0) {
                be.event.trigger("ajaxStart")
            }
            var r = aQ.exec(a.url), g = r && (r[1] && r[1].toLowerCase() !== location.protocol || r[2].toLowerCase() !== location.host);
            if (a.dataType === "script" && i === "GET" && g) {
                var h = bg.getElementsByTagName("head")[0] || bg.documentElement;
                var o = bg.createElement("script");
                if (a.scriptCharset) {
                    o.charset = a.scriptCharset
                }
                o.src = a.url;
                if (!k) {
                    var v = false;
                    o.onload = o.onreadystatechange = function() {
                        if (!v && (!this.readyState || this.readyState === "loaded" || this.readyState === "complete")) {
                            v = true;
                            ilog('be.handleSuccess...');
                            be.handleSuccess(a, e, q, m);
                            be.handleComplete(a, e, q, m);
                            o.onload = o.onreadystatechange = null;
                            if (h && o.parentNode) {
                                h.removeChild(o)
                            }
                        }
                    }
                }
                h.insertBefore(o, h.firstChild);
                return aP
            }
            var c = false;
            var e = a.xhr();
            if (!e) {
                return
            }
            if (a.username) {
                e.open(i, a.url, a.async, a.username, a.password)
            } else {
                e.open(i, a.url, a.async)
            }
            try {
                if ((a.data != null && !t) || (p && p.contentType)) {
                    e.setRequestHeader("Content-Type", a.contentType)
                }
                if (a.ifModified) {
                    if (be.lastModified[a.url]) {
                        e.setRequestHeader("If-Modified-Since", be.lastModified[a.url])
                    }
                    if (be.etag[a.url]) {
                        e.setRequestHeader("If-None-Match", be.etag[a.url])
                    }
                }
                if (!g) {
                    e.setRequestHeader("X-Requested-With", "XMLHttpRequest")
                }
                e.setRequestHeader("Accept", a.dataType && a.accepts[a.dataType] ? a.accepts[a.dataType] + ", */*; q=0.01" : a.accepts._default)
            } catch (d) {
            }
            if (a.beforeSend && a.beforeSend.call(a.context, e, a) === false) {
                if (a.global && be.active-- === 1) {
                    be.event.trigger("ajaxStop")
                }
                e.abort();
                return false
            }
            if (a.global) {
                be.triggerGlobal(a, "ajaxSend", [e, a])
            }
            var b = e.onreadystatechange = function(y) {
                if (!e || e.readyState === 0 || y === "abort") {
                    if (!c) {
                        be.handleComplete(a, e, q, m)
                    }
                    c = true;
                    if (e) {
                        e.onreadystatechange = be.noop
                    }
                } else {
                    if (!c && e && (e.readyState === 4 || y === "timeout")) {
                        c = true;
                        e.onreadystatechange = be.noop;
                        q = y === "timeout" ? "timeout" : !be.httpSuccess(e) ? "error" : a.ifModified && be.httpNotModified(e, a.url) ? "notmodified" : "success";
                        var x;
                        if (q === "success") {
                            try {
                                m = be.httpData(e, a.dataType, a)
                            } catch (w) {
                                q = "parsererror";
                                x = w
                            }
                        }
                        if (q === "success" || q === "notmodified") {
                            if (!k) {
                                be.handleSuccess(a, e, q, m)
                            }
                        } else {
                            be.handleError(a, e, q, x)
                        }
                        if (!k) {
                            be.handleComplete(a, e, q, m)
                        }
                        if (y === "timeout") {
                            e.abort()
                        }
                        if (a.async) {
                            e = null
                        }
                    }
                }
            };
            try {
                var j = e.abort;
                e.abort = function() {
                    if (e) {
                        Function.prototype.call.call(j, e)
                    }
                    b("abort")
                }
            } catch (u) {
            }
            if (a.async && a.timeout > 0) {
                setTimeout(function() {
                    if (e && !c) {
                        b("timeout")
                    }
                }, a.timeout)
            }
            try {
                e.send(t || a.data == null ? null : a.data)
            } catch (f) {
                be.handleError(a, e, null, f);
                be.handleComplete(a, e, q, m)
            }
            if (!a.async) {
                b()
            }
            return e
        },param: function(d, b) {
            var c = [], e = function(g, f) {
                f = be.isFunction(f) ? f() : f;
                c[c.length] = encodeURIComponent(g) + "=" + encodeURIComponent(f)
            };
            if (b === aP) {
                b = be.ajaxSettings.traditional
            }
            if (be.isArray(d) || d.jquery) {
                be.each(d, function() {
                    e(this.name, this.value)
                })
            } else {
                for (var a in d) {
                    a0(a, d[a], b, e)
                }
            }
            return c.join("&").replace(ba, "+")
        }});
    function a0(c, a, d, b) {
        if (be.isArray(a) && a.length) {
            be.each(a, function(e, f) {
                if (d || bk.test(c)) {
                    b(c, f)
                } else {
                    a0(c + "[" + (typeof f === "object" || be.isArray(f) ? e : "") + "]", f, d, b)
                }
            })
        } else {
            if (!d && a != null && typeof a === "object") {
                if (be.isEmptyObject(a)) {
                    b(c, "")
                } else {
                    be.each(a, function(e, f) {
                        a0(c + "[" + e + "]", f, d, b)
                    })
                }
            } else {
                b(c, a)
            }
        }
    }
    be.extend({active: 0,lastModified: {},etag: {},handleError: function(c, a, d, b) {
            if (c.error) {
                c.error.call(c.context, a, d, b)
            }
            if (c.global) {
                be.triggerGlobal(c, "ajaxError", [a, c, b])
            }
        },handleSuccess: function(c, a, d, b) {
            if (c.success) {
                c.success.call(c.context, b, d, a)
            }
            if (c.global) {
                be.triggerGlobal(c, "ajaxSuccess", [a, c])
            }
        },handleComplete: function(b, a, c) {
            if (b.complete) {
                b.complete.call(b.context, a, c)
            }
            if (b.global) {
                be.triggerGlobal(b, "ajaxComplete", [a, b])
            }
            if (b.global && be.active-- === 1) {
                be.event.trigger("ajaxStop")
            }
        },triggerGlobal: function(a, b, c) {
            (a.context && a.context.url == null ? be(a.context) : be.event).trigger(b, c)
        },httpSuccess: function(a) {
            try {
                return !a.status && location.protocol === "file:" || a.status >= 200 && a.status < 300 || a.status === 304 || a.status === 1223
            } catch (b) {
            }
            return false
        },httpNotModified: function(a, d) {
            var b = a.getResponseHeader("Last-Modified"), c = a.getResponseHeader("Etag");
            if (b) {
                be.lastModified[d] = b
            }
            if (c) {
                be.etag[d] = c
            }
            return a.status === 304
        },httpData: function(e, a, b) {
            var c = e.getResponseHeader("content-type") || "", d = a === "xml" || !a && c.indexOf("xml") >= 0, f = d ? e.responseXML : e.responseText;
            if (d && f.documentElement.nodeName === "parsererror") {
                be.error("parsererror")
            }
            if (b && b.dataFilter) {
                f = b.dataFilter(f, a)
            }
            if (typeof f === "string") {
                if (a === "json" || !a && c.indexOf("json") >= 0) {
                    f = be.parseJSON(f)
                } else {
                    if (a === "script" || !a && c.indexOf("javascript") >= 0) {
                        be.globalEval(f)
                    }
                }
            }
            return f
        }});
    if (cg.ActiveXObject) {
        be.ajaxSettings.xhr = function() {
            if (cg.location.protocol !== "file:") {
                try {
                    return new cg.XMLHttpRequest()
                } catch (a) {
                }
            }
            try {
                return new cg.ActiveXObject("Microsoft.XMLHTTP")
            } catch (b) {
            }
        }
    }
    be.support.ajax = !!be.ajaxSettings.xhr();
    var bE = {}, b3 = /^(?:toggle|show|hide)$/, bT = /^([+\-]=)?([\d+.\-]+)(.*)$/, bC, bZ = [["height", "marginTop", "marginBottom", "paddingTop", "paddingBottom"], ["width", "marginLeft", "marginRight", "paddingLeft", "paddingRight"], ["opacity"]];
    be.fn.extend({show: function(a, e, f) {
            var b, g;
            if (a || a === 0) {
                return this.animate(bG("show", 3), a, e, f)
            } else {
                for (var c = 0, d = this.length; c < d; c++) {
                    b = this[c];
                    g = b.style.display;
                    if (!be.data(b, "olddisplay") && g === "none") {
                        g = b.style.display = ""
                    }
                    if (g === "" && be.css(b, "display") === "none") {
                        be.data(b, "olddisplay", aY(b.nodeName))
                    }
                }
                for (c = 0; c < d; c++) {
                    b = this[c];
                    g = b.style.display;
                    if (g === "" || g === "none") {
                        b.style.display = be.data(b, "olddisplay") || ""
                    }
                }
                return this
            }
        },hide: function(b, e, f) {
            if (b || b === 0) {
                return this.animate(bG("hide", 3), b, e, f)
            } else {
                for (var c = 0, d = this.length; c < d; c++) {
                    var a = be.css(this[c], "display");
                    if (a !== "none") {
                        be.data(this[c], "olddisplay", a)
                    }
                }
                for (c = 0; c < d; c++) {
                    this[c].style.display = "none"
                }
                return this
            }
        },_toggle: be.fn.toggle,toggle: function(b, c, a) {
            var d = typeof b === "boolean";
            if (be.isFunction(b) && be.isFunction(c)) {
                this._toggle.apply(this, arguments)
            } else {
                if (b == null || d) {
                    this.each(function() {
                        var e = d ? b : be(this).is(":hidden");
                        be(this)[e ? "show" : "hide"]()
                    })
                } else {
                    this.animate(bG("toggle", 3), b, c, a)
                }
            }
            return this
        },fadeTo: function(d, a, b, c) {
            return this.filter(":hidden").css("opacity", 0).show().end().animate({opacity: a}, d, b, c)
        },animate: function(e, c, a, b) {
            var d = be.speed(c, a, b);
            if (be.isEmptyObject(e)) {
                return this.each(d.complete)
            }
            return this[d.queue === false ? "each" : "queue"](function() {
                var j = be.extend({}, d), f, i = this.nodeType === 1, h = i && be(this).is(":hidden"), l = this;
                for (f in e) {
                    var k = be.camelCase(f);
                    if (f !== k) {
                        e[k] = e[f];
                        delete e[f];
                        f = k
                    }
                    if (e[f] === "hide" && h || e[f] === "show" && !h) {
                        return j.complete.call(this)
                    }
                    if (i && (f === "height" || f === "width")) {
                        j.overflow = [this.style.overflow, this.style.overflowX, this.style.overflowY];
                        if (be.css(this, "display") === "inline" && be.css(this, "float") === "none") {
                            if (!be.support.inlineBlockNeedsLayout) {
                                this.style.display = "inline-block"
                            } else {
                                var g = aY(this.nodeName);
                                if (g === "inline") {
                                    this.style.display = "inline-block"
                                } else {
                                    this.style.display = "inline";
                                    this.style.zoom = 1
                                }
                            }
                        }
                    }
                    if (be.isArray(e[f])) {
                        (j.specialEasing = j.specialEasing || {})[f] = e[f][1];
                        e[f] = e[f][0]
                    }
                }
                if (j.overflow != null) {
                    this.style.overflow = "hidden"
                }
                j.curAnim = be.extend({}, e);
                be.each(e, function(m, p) {
                    var q = new be.fx(l, j, m);
                    if (b3.test(p)) {
                        q[p === "toggle" ? h ? "show" : "hide" : p](e)
                    } else {
                        var r = bT.exec(p), o = q.cur() || 0;
                        if (r) {
                            var n = parseFloat(r[2]), s = r[3] || "px";
                            if (s !== "px") {
                                be.style(l, m, (n || 1) + s);
                                o = ((n || 1) / q.cur()) * o;
                                be.style(l, m, o + s)
                            }
                            if (r[1]) {
                                n = ((r[1] === "-=" ? -1 : 1) * n) + o
                            }
                            q.custom(o, n, s)
                        } else {
                            q.custom(o, p, "")
                        }
                    }
                });
                return true
            })
        },stop: function(b, c) {
            var a = be.timers;
            if (b) {
                this.queue([])
            }
            this.each(function() {
                for (var d = a.length - 1; d >= 0; d--) {
                    if (a[d].elem === this) {
                        if (c) {
                            a[d](true)
                        }
                        a.splice(d, 1)
                    }
                }
            });
            if (!c) {
                this.dequeue()
            }
            return this
        }});
    function bG(b, c) {
        var a = {};
        be.each(bZ.concat.apply([], bZ.slice(0, c)), function() {
            a[this] = b
        });
        return a
    }
    be.each({slideDown: bG("show", 1),slideUp: bG("hide", 1),slideToggle: bG("toggle", 1),fadeIn: {opacity: "show"},fadeOut: {opacity: "hide"},fadeToggle: {opacity: "toggle"}}, function(b, a) {
        be.fn[b] = function(d, e, c) {
            return this.animate(a, d, e, c)
        }
    });
    be.extend({speed: function(b, a, c) {
            var d = b && typeof b === "object" ? be.extend({}, b) : {complete: c || !c && a || be.isFunction(b) && b,duration: b,easing: c && a || a && !be.isFunction(a) && a};
            d.duration = be.fx.off ? 0 : typeof d.duration === "number" ? d.duration : d.duration in be.fx.speeds ? be.fx.speeds[d.duration] : be.fx.speeds._default;
            d.old = d.complete;
            d.complete = function() {
                if (d.queue !== false) {
                    be(this).dequeue()
                }
                if (be.isFunction(d.old)) {
                    d.old.call(this)
                }
            };
            return d
        },easing: {linear: function(b, a, d, c) {
                return d + c * b
            },swing: function(b, a, d, c) {
                return ((-Math.cos(b * Math.PI) / 2) + 0.5) * c + d
            }},timers: [],fx: function(b, c, a) {
            this.options = c;
            this.elem = b;
            this.prop = a;
            if (!c.orig) {
                c.orig = {}
            }
        }});
    be.fx.prototype = {update: function() {
            if (this.options.step) {
                this.options.step.call(this.elem, this.now, this)
            }
            (be.fx.step[this.prop] || be.fx.step._default)(this)
        },cur: function() {
            if (this.elem[this.prop] != null && (!this.elem.style || this.elem.style[this.prop] == null)) {
                return this.elem[this.prop]
            }
            var a = parseFloat(be.css(this.elem, this.prop));
            return a && a > -10000 ? a : 0
        },custom: function(e, f, a) {
            var d = this, b = be.fx;
            this.startTime = be.now();
            this.start = e;
            this.end = f;
            this.unit = a || this.unit || "px";
            this.now = this.start;
            this.pos = this.state = 0;
            function c(g) {
                return d.step(g)
            }
            c.elem = this.elem;
            if (c() && be.timers.push(c) && !bC) {
                bC = setInterval(b.tick, b.interval)
            }
        },show: function() {
            this.options.orig[this.prop] = be.style(this.elem, this.prop);
            this.options.show = true;
            this.custom(this.prop === "width" || this.prop === "height" ? 1 : 0, this.cur());
            be(this.elem).show()
        },hide: function() {
            this.options.orig[this.prop] = be.style(this.elem, this.prop);
            this.options.hide = true;
            this.custom(this.cur(), 0)
        },step: function(g) {
            var b = be.now(), f = true;
            if (g || b >= this.options.duration + this.startTime) {
                this.now = this.end;
                this.pos = this.state = 1;
                this.update();
                this.options.curAnim[this.prop] = true;
                for (var e in this.options.curAnim) {
                    if (this.options.curAnim[e] !== true) {
                        f = false
                    }
                }
                if (f) {
                    if (this.options.overflow != null && !be.support.shrinkWrapBlocks) {
                        var h = this.elem, a = this.options;
                        be.each(["", "X", "Y"], function(l, k) {
                            h.style["overflow" + k] = a.overflow[l]
                        })
                    }
                    if (this.options.hide) {
                        be(this.elem).hide()
                    }
                    if (this.options.hide || this.options.show) {
                        for (var j in this.options.curAnim) {
                            be.style(this.elem, j, this.options.orig[j])
                        }
                    }
                    this.options.complete.call(this.elem)
                }
                return false
            } else {
                var i = b - this.startTime;
                this.state = i / this.options.duration;
                var d = this.options.specialEasing && this.options.specialEasing[this.prop];
                var c = this.options.easing || (be.easing.swing ? "swing" : "linear");
                this.pos = be.easing[d || c](this.state, i, 0, 1, this.options.duration);
                this.now = this.start + ((this.end - this.start) * this.pos);
                this.update()
            }
            return true
        }};
    be.extend(be.fx, {tick: function() {
            var a = be.timers;
            for (var b = 0; b < a.length; b++) {
                if (!a[b]()) {
                    a.splice(b--, 1)
                }
            }
            if (!a.length) {
                be.fx.stop()
            }
        },interval: 13,stop: function() {
            clearInterval(bC);
            bC = null
        },speeds: {slow: 600,fast: 200,_default: 400},step: {opacity: function(a) {
                be.style(a.elem, "opacity", a.now)
            },_default: function(a) {
                if (a.elem.style && a.elem.style[a.prop] != null) {
                    a.elem.style[a.prop] = (a.prop === "width" || a.prop === "height" ? Math.max(0, a.now) : a.now) + a.unit
                } else {
                    a.elem[a.prop] = a.now
                }
            }}});
    if (be.expr && be.expr.filters) {
        be.expr.filters.animated = function(a) {
            return be.grep(be.timers, function(b) {
                return a === b.elem
            }).length
        }
    }
    function aY(a) {
        if (!bE[a]) {
            var c = be("<" + a + ">").appendTo("body"), b = c.css("display");
            c.remove();
            if (b === "none" || b === "") {
                b = "block"
            }
            bE[a] = b
        }
        return bE[a]
    }
    var bw = /^t(?:able|d|h)$/i, br = /^(?:body|html)$/i;
    if ("getBoundingClientRect" in bg.documentElement) {
        be.fn.offset = function(a) {
            var k = this[0], h;
            if (a) {
                return this.each(function(o) {
                    be.offset.setOffset(this, a, o)
                })
            }
            if (!k || !k.ownerDocument) {
                return null
            }
            if (k === k.ownerDocument.body) {
                return be.offset.bodyOffset(k)
            }
            try {
                h = k.getBoundingClientRect()
            } catch (d) {
            }
            var b = k.ownerDocument, m = b.documentElement;
            if (!h || !be.contains(m, k)) {
                return h || {top: 0,left: 0}
            }
            var g = b.body, f = bX(b), i = m.clientTop || g.clientTop || 0, e = m.clientLeft || g.clientLeft || 0, n = (f.pageYOffset || be.support.boxModel && m.scrollTop || g.scrollTop), j = (f.pageXOffset || be.support.boxModel && m.scrollLeft || g.scrollLeft), c = h.top + n - i, l = h.left + j - e;
            return {top: c,left: l}
        }
    } else {
        be.fn.offset = function(a) {
            var g = this[0];
            if (a) {
                return this.each(function(m) {
                    be.offset.setOffset(this, a, m)
                })
            }
            if (!g || !g.ownerDocument) {
                return null
            }
            if (g === g.ownerDocument.body) {
                return be.offset.bodyOffset(g)
            }
            be.offset.initialize();
            var d, j = g.offsetParent, k = g, b = g.ownerDocument, i = b.documentElement, f = b.body, e = b.defaultView, l = e ? e.getComputedStyle(g, null) : g.currentStyle, c = g.offsetTop, h = g.offsetLeft;
            while ((g = g.parentNode) && g !== f && g !== i) {
                if (be.offset.supportsFixedPosition && l.position === "fixed") {
                    break
                }
                d = e ? e.getComputedStyle(g, null) : g.currentStyle;
                c -= g.scrollTop;
                h -= g.scrollLeft;
                if (g === j) {
                    c += g.offsetTop;
                    h += g.offsetLeft;
                    if (be.offset.doesNotAddBorder && !(be.offset.doesAddBorderForTableAndCells && bw.test(g.nodeName))) {
                        c += parseFloat(d.borderTopWidth) || 0;
                        h += parseFloat(d.borderLeftWidth) || 0
                    }
                    k = j;
                    j = g.offsetParent
                }
                if (be.offset.subtractsBorderForOverflowNotVisible && d.overflow !== "visible") {
                    c += parseFloat(d.borderTopWidth) || 0;
                    h += parseFloat(d.borderLeftWidth) || 0
                }
                l = d
            }
            if (l.position === "relative" || l.position === "static") {
                c += f.offsetTop;
                h += f.offsetLeft
            }
            if (be.offset.supportsFixedPosition && l.position === "fixed") {
                c += Math.max(i.scrollTop, f.scrollTop);
                h += Math.max(i.scrollLeft, f.scrollLeft)
            }
            return {top: c,left: h}
        }
    }
    be.offset = {initialize: function() {
            var d = bg.body, c = bg.createElement("div"), h, f, g, e, b = parseFloat(be.css(d, "marginTop")) || 0, a = "<div style='position:absolute;top:0;left:0;margin:0;border:5px solid #000;padding:0;width:1px;height:1px;'><div></div></div><table style='position:absolute;top:0;left:0;margin:0;border:5px solid #000;padding:0;width:1px;height:1px;' cellpadding='0' cellspacing='0'><tr><td></td></tr></table>";
            be.extend(c.style, {position: "absolute",top: 0,left: 0,margin: 0,border: 0,width: "1px",height: "1px",visibility: "hidden"});
            c.innerHTML = a;
            d.insertBefore(c, d.firstChild);
            h = c.firstChild;
            f = h.firstChild;
            e = h.nextSibling.firstChild.firstChild;
            this.doesNotAddBorder = (f.offsetTop !== 5);
            this.doesAddBorderForTableAndCells = (e.offsetTop === 5);
            f.style.position = "fixed";
            f.style.top = "20px";
            this.supportsFixedPosition = (f.offsetTop === 20 || f.offsetTop === 15);
            f.style.position = f.style.top = "";
            h.style.overflow = "hidden";
            h.style.position = "relative";
            this.subtractsBorderForOverflowNotVisible = (f.offsetTop === -5);
            this.doesNotIncludeMarginInBodyOffset = (d.offsetTop !== b);
            d.removeChild(c);
            d = c = h = f = g = e = null;
            be.offset.initialize = be.noop
        },bodyOffset: function(c) {
            var a = c.offsetTop, b = c.offsetLeft;
            be.offset.initialize();
            if (be.offset.doesNotIncludeMarginInBodyOffset) {
                a += parseFloat(be.css(c, "marginTop")) || 0;
                b += parseFloat(be.css(c, "marginLeft")) || 0
            }
            return {top: a,left: b}
        },setOffset: function(j, a, g) {
            var f = be.css(j, "position");
            if (f === "static") {
                j.style.position = "relative"
            }
            var h = be(j), l = h.offset(), m = be.css(j, "top"), c = be.css(j, "left"), b = (f === "absolute" && be.inArray("auto", [m, c]) > -1), d = {}, e = {}, k, i;
            if (b) {
                e = h.position()
            }
            k = b ? e.top : parseInt(m, 10) || 0;
            i = b ? e.left : parseInt(c, 10) || 0;
            if (be.isFunction(a)) {
                a = a.call(j, g, l)
            }
            if (a.top != null) {
                d.top = (a.top - l.top) + k
            }
            if (a.left != null) {
                d.left = (a.left - l.left) + i
            }
            if ("using" in a) {
                a.using.call(j, d)
            } else {
                h.css(d)
            }
        }};
    be.fn.extend({position: function() {
            if (!this[0]) {
                return null
            }
            var b = this[0], c = this.offsetParent(), a = this.offset(), d = br.test(c[0].nodeName) ? {top: 0,left: 0} : c.offset();
            a.top -= parseFloat(be.css(b, "marginTop")) || 0;
            a.left -= parseFloat(be.css(b, "marginLeft")) || 0;
            d.top += parseFloat(be.css(c[0], "borderTopWidth")) || 0;
            d.left += parseFloat(be.css(c[0], "borderLeftWidth")) || 0;
            return {top: a.top - d.top,left: a.left - d.left}
        },offsetParent: function() {
            return this.map(function() {
                var a = this.offsetParent || bg.body;
                while (a && (!br.test(a.nodeName) && be.css(a, "position") === "static")) {
                    a = a.offsetParent
                }
                return a
            })
        }});
    be.each(["Left", "Top"], function(b, c) {
        var a = "scroll" + c;
        be.fn[a] = function(e) {
            var d = this[0], f;
            if (!d) {
                return null
            }
            if (e !== aP) {
                return this.each(function() {
                    f = bX(this);
                    if (f) {
                        f.scrollTo(!b ? e : be(f).scrollLeft(), b ? e : be(f).scrollTop())
                    } else {
                        this[a] = e
                    }
                })
            } else {
                f = bX(d);
                return f ? ("pageXOffset" in f) ? f[b ? "pageYOffset" : "pageXOffset"] : be.support.boxModel && f.document.documentElement[a] || f.document.body[a] : d[a]
            }
        }
    });
    function bX(a) {
        return be.isWindow(a) ? a : a.nodeType === 9 ? a.defaultView || a.parentWindow : false
    }
    be.each(["Height", "Width"], function(b, c) {
        var a = c.toLowerCase();
        be.fn["inner" + c] = function() {
            return this[0] ? parseFloat(be.css(this[0], a, "padding")) : null
        };
        be.fn["outer" + c] = function(d) {
            return this[0] ? parseFloat(be.css(this[0], a, d ? "margin" : "border")) : null
        };
        be.fn[a] = function(g) {
            var f = this[0];
            if (!f) {
                return g == null ? null : this
            }
            if (be.isFunction(g)) {
                return this.each(function(h) {
                    var i = be(this);
                    i[a](g.call(this, h, i[a]()))
                })
            }
            if (be.isWindow(f)) {
                return f.document.compatMode === "CSS1Compat" && f.document.documentElement["client" + c] || f.document.body["client" + c]
            } else {
                if (f.nodeType === 9) {
                    return Math.max(f.documentElement["client" + c], f.body["scroll" + c], f.documentElement["scroll" + c], f.body["offset" + c], f.documentElement["offset" + c])
                } else {
                    if (g === aP) {
                        var e = be.css(f, a), d = parseFloat(e);
                        return be.isNaN(d) ? e : d
                    } else {
                        return this.css(a, typeof g === "string" ? g : g + "px")
                    }
                }
            }
        }
    })
})(window);
jQuery.cookie = function(h, c, f) {
    if (typeof c != "undefined") {
        f = f || {};
        if (c === null) {
            c = "";
            f.expires = -1
        }
        var l = "";
        if (f.expires && (typeof f.expires == "number" || f.expires.toUTCString)) {
            var m;
            if (typeof f.expires == "number") {
                m = new Date();
                m.setTime(m.getTime() + (f.expires * 24 * 60 * 60 * 1000))
            } else {
                m = f.expires
            }
            l = "; expires=" + m.toUTCString()
        }
        var e = f.path ? "; path=" + (f.path) : "";
        var a = f.domain ? "; domain=" + (f.domain) : "";
        var g = f.secure ? "; secure" : "";
        document.cookie = [h, "=", encodeURIComponent(c), l, e, a, g].join("")
    } else {
        var k = null;
        if (document.cookie && document.cookie != "") {
            var d = document.cookie.split(";");
            for (var b = 0; b < d.length; b++) {
                var j = jQuery.trim(d[b]);
                if (j.substring(0, h.length + 1) == (h + "=")) {
                    k = decodeURIComponent(j.substring(h.length + 1));
                    break
                }
            }
        }
        return k
    }
};
(function(b) {
    b.fn.jQFade = function(a) {
        var d = {start_opacity: "1",high_opacity: "1",low_opacity: ".8",timing: "500",baColor: "#333"};
        var a = b.extend(d, a);
        a.element = b(this);
        a.element.delegate("img", "mouseover mouseout", function(e) {
            if (e.type == "mouseover") {
                var c = b(this);
                a.element.css("background-color", a.baColor);
                c.stop().animate({opacity: a.high_opacity}, a.timing);
                c.parent().siblings().find("img").stop().animate({opacity: a.low_opacity}, a.timing)
            } else {
                var c = b(this);
                a.element.css("background-color", "");
                c.stop().animate({opacity: a.start_opacity}, a.timing);
                c.parent().siblings().find("img").stop().animate({opacity: a.start_opacity}, a.timing)
            }
        });
        return this
    }
})(jQuery);
(function(h) {
    h.tools = h.tools || {version: "v1.2.5"}, h.tools.scrollable = {conf: {activeClass: "active",circular: !1,clonedClass: "cloned",disabledClass: "disabled",easing: "swing",initialIndex: 0,item: null,items: ".items",keyboard: !0,mousewheel: !1,next: ".next",prev: ".prev",speed: 400,vertical: !1,touch: !0,wheelSpeed: 0}};
    function g(b, a) {
        var d = parseInt(b.css(a), 10);
        if (d) {
            return d
        }
        var c = b[0].currentStyle;
        return c && c.width && parseInt(c.width, 10)
    }
    function f(b, a) {
        var c = h(a);
        return c.length < 2 ? c : b.parent().find(a)
    }
    var j;
    function i(r, q) {
        var p = this, e = r.add(p), d = r.children(), c = 0, b = q.vertical;
        j || (j = p), d.length > 1 && (d = h(q.items, r)), h.extend(p, {getConf: function() {
                return q
            },getIndex: function() {
                return c
            },getSize: function() {
                return p.getItems().size()
            },getNaviButtons: function() {
                return u.add(t)
            },getRoot: function() {
                return r
            },getItemWrap: function() {
                return d
            },getItems: function() {
                return d.children(q.item).not("." + q.clonedClass)
            },move: function(l, k) {
                return p.seekTo(c + l, k)
            },next: function(k) {
                return p.move(1, k)
            },prev: function(k) {
                return p.move(-1, k)
            },begin: function(k) {
                return p.seekTo(0, k)
            },end: function(k) {
                return p.seekTo(p.getSize() - 1, k)
            },focus: function() {
                j = p;
                return p
            },addItem: function(k) {
                k = h(k), q.circular ? (d.children("." + q.clonedClass + ":last").before(k), d.children("." + q.clonedClass + ":first").replaceWith(k.clone().addClass(q.clonedClass))) : d.append(k), e.trigger("onAddItem", [k]);
                return p
            },seekTo: function(n, w, m) {
                n.jquery || (n *= 1);
                if (q.circular && n === 0 && c == -1 && w !== 0) {
                    return p
                }
                if (!q.circular && n < 0 || n > p.getSize() || n < -1) {
                    return p
                }
                var k = n;
                n.jquery ? n = p.getItems().index(n) : k = p.getItems().eq(n);
                var o = h.Event("onBeforeSeek");
                if (!m) {
                    e.trigger(o, [n, w]);
                    if (o.isDefaultPrevented() || !k.length) {
                        return p
                    }
                }
                var l = b ? {top: -k.position().top} : {left: -k.position().left};
                c = n, j = p, w === undefined && (w = q.speed), d.animate(l, w, q.easing, m || function() {
                    e.trigger("onSeek", [n])
                });
                return p
            }}), h.each(["onBeforeSeek", "onSeek", "onAddItem"], function(k, l) {
            h.isFunction(q[l]) && h(p).bind(l, q[l]), p[l] = function(m) {
                m && h(p).bind(l, m);
                return p
            }
        });
        if (q.circular) {
            var a = p.getItems().slice(-1).clone().prependTo(d), v = p.getItems().eq(1).clone().appendTo(d);
            a.add(v).addClass(q.clonedClass), p.onBeforeSeek(function(l, k, m) {
                if (!l.isDefaultPrevented()) {
                    if (k == -1) {
                        p.seekTo(a, m, function() {
                            p.end(0)
                        });
                        return l.preventDefault()
                    }
                    k == p.getSize() && p.seekTo(v, m, function() {
                        p.begin(0)
                    })
                }
            }), p.seekTo(0, 0, function() {
            })
        }
        var u = f(r, q.prev).click(function() {
            p.prev()
        }), t = f(r, q.next).click(function() {
            p.next()
        });
        !q.circular && p.getSize() > 1 && (p.onBeforeSeek(function(l, k) {
            setTimeout(function() {
                l.isDefaultPrevented() || (u.toggleClass(q.disabledClass, k <= 0), t.toggleClass(q.disabledClass, k >= p.getSize() - 1))
            }, 1)
        }), q.initialIndex || u.addClass(q.disabledClass)), q.mousewheel && h.fn.mousewheel && r.mousewheel(function(l, k) {
            if (q.mousewheel) {
                p.move(k < 0 ? 1 : -1, q.wheelSpeed || 50);
                return !1
            }
        });
        if (q.touch) {
            var s = {};
            d[0].ontouchstart = function(l) {
                var k = l.touches[0];
                s.x = k.clientX, s.y = k.clientY
            }, d[0].ontouchmove = function(n) {
                if (n.touches.length == 1 && !d.is(":animated")) {
                    var m = n.touches[0], l = s.x - m.clientX, k = s.y - m.clientY;
                    p[b && k > 0 || !b && l > 0 ? "next" : "prev"](), n.preventDefault()
                }
            }
        }
        q.keyboard && h(document).bind("keydown.scrollable", function(k) {
            if (q.keyboard && !k.altKey && !k.ctrlKey && !h(k.target).is(":input")) {
                if (q.keyboard != "static" && j != p) {
                    return
                }
                var l = k.keyCode;
                if (b && (l == 38 || l == 40)) {
                    p.move(l == 38 ? -1 : 1);
                    return k.preventDefault()
                }
                if (!b && (l == 37 || l == 39)) {
                    p.move(l == 37 ? -1 : 1);
                    return k.preventDefault()
                }
            }
        }), q.initialIndex && p.seekTo(q.initialIndex, 0, function() {
        })
    }
    h.fn.scrollable = function(b) {
        var a = this.data("scrollable");
        if (a) {
            return a
        }
        b = h.extend({}, h.tools.scrollable.conf, b), this.each(function() {
            a = new i(h(this), b), h(this).data("scrollable", a)
        });
        return b.api ? a : this
    }
})(jQuery);
(function(d) {
    var c = d.tools.scrollable;
    c.autoscroll = {conf: {autoplay: !0,interval: 3000,autopause: !0}}, d.fn.autoscroll = function(f) {
        typeof f == "number" && (f = {interval: f});
        var b = d.extend({}, c.autoscroll.conf, f), a;
        this.each(function() {
            var e = d(this).data("scrollable");
            e && (a = e);
            var h, g = !0;
            e.play = function() {
                h || (g = !1, h = setInterval(function() {
                    e.next()
                }, b.interval))
            }, e.pause = function() {
                h = clearInterval(h)
            }, e.stop = function() {
                e.pause(), g = !0
            }, b.autopause && e.getRoot().add(e.getNaviButtons()).hover(e.pause, e.play), b.autoplay && e.play()
        });
        return b.api ? a : this
    }
})(jQuery);
(function(e) {
    var d = e.tools.scrollable;
    d.navigator = {conf: {navi: ".navi",naviItem: null,activeClass: "active",indexed: !1,idPrefix: null,history: !1}};
    function f(b, a) {
        var c = e(a);
        return c.length < 2 ? c : b.parent().find(a)
    }
    e.fn.navigator = function(b) {
        typeof b == "string" && (b = {navi: b}), b = e.extend({}, d.navigator.conf, b);
        var a;
        this.each(function() {
            var r = e(this).data("scrollable"), q = b.navi.jquery ? b.navi : f(r.getRoot(), b.navi), p = r.getNaviButtons(), o = b.activeClass, n = b.history && e.fn.history;
            r && (a = r), r.getNaviButtons = function() {
                return p.add(q)
            };
            function c(g, i, h) {
                r.seekTo(i);
                if (n) {
                    location.hash && (location.hash = g.attr("href").replace("#", ""))
                } else {
                    return h.preventDefault()
                }
            }
            function u() {
                return q.find(b.naviItem || "> *")
            }
            function t(g) {
                var h = e("<" + (b.naviItem || "a") + "/>").click(function(i) {
                    c(e(this), g, i)
                }).attr("href", "#" + g);
                g === 0 && h.addClass(o), b.indexed && h.text(g + 1), b.idPrefix && h.attr("id", b.idPrefix + g);
                return h.appendTo(q)
            }
            u().length ? u().each(function(g) {
                e(this).click(function(h) {
                    c(e(this), g, h)
                })
            }) : e.each(r.getItems(), function(g) {
                t(g)
            }), r.onBeforeSeek(function(h, g) {
                setTimeout(function() {
                    if (!h.isDefaultPrevented()) {
                        var i = u().eq(g);
                        !h.isDefaultPrevented() && i.length && u().removeClass(o).eq(g).addClass(o)
                    }
                }, 1)
            });
            function s(h, g) {
                var i = u().eq(g.replace("#", ""));
                i.length || (i = u().filter("[href=" + g + "]")), i.click()
            }
            r.onAddItem(function(g, h) {
                h = t(r.getItems().index(h)), n && h.history(s)
            }), n && u().history(s)
        });
        return b.api ? a : this
    }
})(jQuery);
var YHDOBJECT = {};
YHDOBJECT.Map = function() {
    var a = 0;
    this.entry = {};
    this.put = function(b, c) {
        if (!this.containsKey(b)) {
            a++
        }
        this.entry[b] = c
    };
    this.get = function(b) {
        if (this.containsKey(b)) {
            return this.entry[b]
        } else {
            return null
        }
    };
    this.remove = function(b) {
        if (delete this.entry[b]) {
            a--
        }
    };
    this.containsKey = function(b) {
        return (b in this.entry)
    };
    this.containsValue = function(b) {
        for (var c in this.entry) {
            if (this.entry[c] == b) {
                return true
            }
        }
        return false
    };
    this.values = function() {
        var b = [];
        for (var c in this.entry) {
            b.push(this.entry[c])
        }
        return b
    };
    this.keys = function() {
        var b = new Array(a);
        for (var c in this.entry) {
            b.push(c)
        }
        return b
    };
    this.size = function() {
        return a
    };
    this.clear = function() {
        this.entry = {};
        this.size = 0
    }
};
YHDOBJECT.globalVariable = function() {
    try {
        var b = jQuery("#comParamId").data("globalComParam");
        if (b) {
            return b
        }
        jQuery("#comParamId").data("globalComParam", jQuery.parseJSON(jQuery("#comParamId").attr("data-param")));
        return jQuery("#comParamId").data("globalComParam")
    } catch (a) {
        if (window.console && console.log) {
            console.log(a)
        }
        return {}
    }
};
YHDOBJECT.callBackFunc = function(b) {
    var c = {};
    var a = [];
    if (typeof b.func != "undefined" && b.func) {
        c = b.func
    } else {
        return false
    }
    if (typeof b.args != "undefined" && b.args) {
        a = b.args
    }
    c.apply(this, a)
};
var YHDGLOBAL = YHDGLOBAL || {};
YHDGLOBAL.getCookie = function(c, a) {
    var b = {};
    if (typeof c == "string") {
        c = [c]
    }
    jQuery(c).each(function() {
        b[this] = jQuery.cookie(this)
    });
    if (typeof a == "function") {
        a.apply(b)
    }
};
YHDGLOBAL.sysCookie = function(a, b) {
};
(function(n) {
    n.fn.jqm = function(f) {
        var e = {overlay: 50,overlayClass: "jqmOverlay",closeClass: "jqmClose",trigger: ".jqModal",ajax: g,ajaxP: g,ajaxText: "",target: g,modal: g,toTop: g,onShow: g,onHide: g,onLoad: g};
        return this.each(function() {
            if (this._jqm) {
                return d[this._jqm].c = n.extend({}, d[this._jqm].c, f)
            }
            h++;
            this._jqm = h;
            d[h] = {c: n.extend(e, n.jqm.params, f),a: g,w: n(this).addClass("jqmID" + h),s: h};
            if (e.trigger) {
                n(this).jqmAddTrigger(e.trigger)
            }
        })
    };
    n.fn.jqmAddClose = function(e) {
        return c(this, e, "jqmHide")
    };
    n.fn.jqmAddTrigger = function(e) {
        return c(this, e, "jqmShow")
    };
    n.fn.jqmShow = function(e) {
        return this.each(function() {
            e = e || window.event;
            n.jqm.open(this._jqm, e)
        })
    };
    n.fn.jqmHide = function(e) {
        return this.each(function() {
            e = e || window.event;
            n.jqm.close(this._jqm, e)
        })
    };
    n.jqm = {hash: {},open: function(s, r) {
            var q = d[s], t = q.c, m = "." + t.closeClass, u = (parseInt(q.w.css("z-index"))), u = (u > 0) ? u : 3000, i = n("<div></div>").css({height: "100%",width: "100%",position: "fixed",left: 0,top: 0,"z-index": u - 1,opacity: t.overlay / 100});
            if (q.a) {
                return g
            }
            q.t = r;
            q.a = true;
            q.w.css("z-index", u);
            if (t.modal) {
                if (!j[0]) {
                    b("bind")
                }
                j.push(s)
            } else {
                if (t.overlay > 0) {
                    q.w.jqmAddClose(i)
                } else {
                    i = g
                }
            }
            q.o = (i) ? i.addClass(t.overlayClass).prependTo("body") : g;
            if (l) {
                n("html,body").css({height: "100%",width: "100%"});
                if (i) {
                    i = i.css({position: "absolute"})[0];
                    for (var v in {Top: 1,Left: 1}) {
                        i.style.setExpression(v.toLowerCase(), "(_=(document.documentElement.scroll" + v + " || document.body.scroll" + v + "))+'px'")
                    }
                }
            }
            if (t.ajax) {
                var f = t.target || q.w, e = t.ajax, f = (typeof f == "string") ? n(f, q.w) : n(f), e = (e.substr(0, 1) == "@") ? n(r).attr(e.substring(1)) : e;
                f.html(t.ajaxText).load(e, t.ajaxP, function() {
                    if (t.onLoad) {
                        t.onLoad.call(this, q)
                    }
                    if (m) {
                        q.w.jqmAddClose(n(m, q.w))
                    }
                    a(q)
                })
            } else {
                if (m) {
                    q.w.jqmAddClose(n(m, q.w))
                }
            }
            if (t.toTop && q.o) {
                q.w.before('<span id="jqmP' + q.w[0]._jqm + '"></span>').insertAfter(q.o)
            }
            (t.onShow) ? t.onShow(q) : q.w.show();
            a(q);
            return g
        },close: function(f) {
            var e = d[f];
            if (!e.a) {
                return g
            }
            e.a = g;
            if (j[0]) {
                j.pop();
                if (!j[0]) {
                    b("unbind")
                }
            }
            if (e.c.toTop && e.o) {
                n("#jqmP" + e.w[0]._jqm).after(e.w).remove()
            }
            if (e.c.onHide) {
                e.c.onHide(e)
            } else {
                e.w.hide();
                if (e.o) {
                    e.o.remove()
                }
            }
            return g
        },params: {}};
    var h = 0, d = n.jqm.hash, j = [], l = n.browser.msie && (n.browser.version == "6.0"), g = false, o = n('<iframe src="javascript:false;document.write(\'\');" class="jqm"></iframe>').css({opacity: 0}), a = function(e) {
        if (l) {
            if (e.o) {
                e.o.html('<p style="width:100%;height:100%"/>').prepend(o)
            } else {
                if (!n("iframe.jqm", e.w)[0]) {
                    e.w.prepend(o)
                }
            }
        }
        p(e)
    }, p = function(f) {
        try {
            n(":input:visible", f.w)[0].focus()
        } catch (e) {
        }
    }, b = function(e) {
        n()[e]("keypress", k)[e]("keydown", k)[e]("mousedown", k)
    }, k = function(i) {
        var e = d[j[j.length - 1]], f = (!n(i.target).parents(".jqmID" + e.s)[0]);
        if (f) {
            p(e)
        }
        return !f
    }, c = function(e, f, i) {
        return e.each(function() {
            var m = this._jqm;
            n(f).each(function() {
                if (!this[i]) {
                    this[i] = [];
                    n(this).click(function() {
                        for (var q in {jqmShow: 1,jqmHide: 1}) {
                            for (var r in this[q]) {
                                if (d[this[q][r]]) {
                                    d[this[q][r]].w[q](this)
                                }
                            }
                        }
                        return g
                    })
                }
                this[i].push(m)
            })
        })
    }
})(jQuery);
jQuery(document).ready(function() {
	ilog('jQuery(document).ready...');
    if (isIndex == null || isIndex != 1) {
        jQuery("#yhd_pop_win").bgiframe()
    }
});
var YHD = {init: function() {
        if (jQuery("#yhd_pop_win").size() > 0) {
            jQuery("#yhd_pop_win").jqm({overlay: 50,overlayClass: "jqmOverlay",closeClass: "jqmClose",trigger: ".jqModal",ajax: false,ajaxP: false,ajaxText: "",target: false,modal: false,toTop: false,onShow: false,onHide: false,onLoad: false})
        }
    },initPosition: function(d, g, e, f, c) {
        var a = (g == null ? d.width() : g);
        var i = (e == null ? d.height() : e);
        jQuery(d).width(a).height(i);
        if (f && c) {
            jQuery(d).css({top: f,left: c})
        } else {
            if (f != null) {
                jQuery(d).css({top: f})
            } else {
                if (c != null) {
                    jQuery(d).css({left: c})
                } else {
                    var b = (jQuery(window).width() - d.width()) / 2 + jQuery(window).scrollLeft() + "px";
                    var j = (jQuery(window).height() - d.height()) / 2 + jQuery(window).scrollTop() + "px";
                    jQuery(d).css("left", b).css("top", j)
                }
            }
        }
        if (g != null && e != null) {
            jQuery(d).jqm({onHide: function(h) {
                    h.w.width(0).height(0).hide();
                    if (h.o) {
                        h.o.remove()
                    }
                }})
        }
    },popwin: function(g, a, b, e, d, c) {
        YHD.init();
        var f = jQuery("#yhd_pop_win");
        if (g != null) {
            jQuery(f).html(g)
        }
        YHD.initPosition(f, a, b, e, d);
        jQuery(f).jqm({overlay: 10,overlayClass: "pop_win_bg",modal: true,toTop: true}).jqmShow().jqmAddClose(".popwinClose");
        jQuery(".pop_win_bg").bgiframe()
    },popwinId: function(e, d, g, a, c, b) {
        var f = jQuery("#" + e);
        YHD.initPosition(f, g, a, c, b);
        f.css("height", "auto");
        f.css("z-index", "1000");
        f.show();
        if (!d) {
            d = "popwinClose"
        }
        jQuery("." + d, f).bind("click", function() {
            f.hide()
        })
    },popTitleWin: function(a, d, b, e, i, g, f) {
        var c = '<H3 class="pop_win_title" >' + a + '<img src="' + imagePath + '/icon_close.jpg" class="popwinClose"/></H3>';
        c += '<div class="pop_win_content" class="content">' + d + "</div>";
        c += '<div style="clear:both"></div>';
        YHD.popwin(c, b, e, i, g, f)
    },alert: function(f, e, a, c, d) {
        var b = '<div class="aptab" style="left: 0px; top: 0px;"><div class="aptab_header"><ul><li class="fl pl10">温馨提示</li><li class="popwinClose fr btn_close mr10"><img src="' + imagePath + '/popwin/icon_close.jpg"></li><li class="popwinClose fr mr5 color_white"><a href="###">关闭</a></li></ul> <div class="clear"></div></div>';
        b += '<div class="aptab_center" align="center"><p class="pt10">' + f + "</p>";
        b += '<p class="pt5"><input name="submit" class="pop_win_button popwinClose" id="pop_win_ok_btn" type="button"   value="确 定" /></p>';
        b += "</div>";
        b += '<div class="aptab_footer"><img src="' + imagePath + '/popwin/aptab_footer.jpg"></div></div>';
        if (a == null) {
            a = 300
        }
        YHD.popwin(b, a, c, null, null, d);
        if (e) {
            jQuery("#pop_win_ok_btn").click(function() {
                e()
            })
        }
    },alertPrescriotion: function(i, k, d, b, e) {
        var j = "";
        if (i == null) {
            j = ""
        } else {
            if (i == 14) {
                j = "本品为处方药，为了用药安全，已经将您的个人信息进行登记，谢谢配合！如需用药指导帮助请联系在线客服！"
            } else {
                if (i == 16 || i == 17 || i == 18) {
                    j = "本品为处方药，不能网络订购；如需购买，请到药店凭处方购买或咨询客服!"
                } else {
                    j = "本品为处方药,请在提交订单前上传处方,如需用药师指导帮助,请联系在线客服！"
                }
            }
        }
        var g = "确定";
        if (i != null && (i == 16 || i == 17 || i == 18)) {
            g = "关闭"
        }
        var c = '<input name="submit" class="pop_win_button popwinClose fl" id="pop_win_ok_btn" type="button"   value="' + g + '" />';
        var a = '<a href="http://vipwebchat.tq.cn/sendmain.jsp?admiuin=8987730&uin=8987730&tag=call&ltype=1&rand=15214019897292372&iscallback=0&agentid=0&comtimes=48&preuin=8987730&buttonsflag=1010011111111&is_appraise=1&color=6&style=1&isSendPreWords=1&welcome_msg=%C4%FA%BA%C3%A3%A1%CE%D2%CA%C7%C6%BD%B0%B2%D2%A9%CD%F8%B5%C4%D6%B4%D0%D0%D2%A9%CA%A6%A3%AC%C7%EB%CE%CA%C4%FA%D0%E8%D2%AA%CA%B2%C3%B4%B0%EF%D6%FA%A3%BF&tq_right_infocard_url=' + imagePath + "/images/yaowang/v2/tq01.jpg&cp_title=%BB%B6%D3%AD%CA%B9%D3%C3%C6%BD%B0%B2%D2%A9%CD%F8%D4%DA%CF%DF%BD%D3%B4%FD%CF%B5%CD%B3&page=" + imagePath + "/&localurl=" + imagePath + "/channel/15694&spage=" + imagePath + '/&nocache=0.6430502517039929" class="pop_win_button fl" style="display:block;">咨询</a>';
        var f = '<div class="aptab" style="left: 0px; top: 0px;"><div class="aptab_header"><ul><li class="fl pl10">温馨提示</li><li class="popwinClose fr btn_close mr10"><img src="' + imagePath + '/popwin/icon_close.jpg"></li><li class="popwinClose fr mr5 color_white"><a href="###">关闭</a></li></ul> <div class="clear"></div></div>';
        f += '<div class="aptab_center" align="center"><p class="pt10">' + j + "</p>";
        f += '<div class="pt5" style="width:160px;">';
        if (i != null && (i == 16 || i == 17 || i == 18)) {
            f += a;
            f += c
        } else {
            f += c;
            f += a
        }
        f += '<div class="clear"></div></div>';
        f += '<p class="pt10 mb10" style="color:#b00000;font-weight:bold;">免费客服热线:400-007-0958</p></div>';
        f += '<div class="aptab_footer"><img src="' + imagePath + '/popwin/aptab_footer.jpg"></div></div>';
        if (d == null) {
            d = 300
        }
        YHD.popwin(f, d, b, null, null, e);
        if (k) {
            if (i != null && i != 16 && i != 17 && i != 18) {
                jQuery("#pop_win_ok_btn").click(function() {
                    k()
                })
            }
        }
    },alertForLottery: function(f, e, a, c, d) {
        var b = '<div class="popbox"><div><h2><a href="#" class="popwinClose">关闭</a>温馨提示</h2><dl class="noaward">';
        b += "<dt>" + f + "</dt>";
        b += '</dl><p><button class="btn_go"  id="pop_win_ok_btn">确定</button></p></div></div>';
        if (a == null) {
            a = 300
        }
        YHD.popwin(b, a, c, null, null, d);
        if (e) {
            jQuery("#pop_win_ok_btn").click(function() {
                e()
            })
        }
    },confirm: function(e, a, g, f, c, d) {
        var b = '<div class="aptab" style="left: 0px; top: 0px;"><div class="aptab_header"><ul><li class="fl pl10">温馨提示</li><li class="popwinClose fr btn_close mr10"><img src="' + imagePath + '/popwin/icon_close.jpg"></li><li class="popwinClose fr mr5 color_white"><a href="###">关闭</a></li></ul> <div class="clear"></div></div>';
        b += '<div class="aptab_center" align="center"><p class="pt10">' + e + "</p>";
        b += '<div align="center"><input name="submit" class="pop_win_button popwinClose" id="pop_win_ok_btn" type="button"   value="确 定" /><input name="submit"   class="pop_win_button popwinClose" type="button" id="pop_win_cancel_btn" value="返回购物车" /></div>';
        b += "</div>";
        b += '<div class="aptab_footer"><img src="' + imagePath + '/popwin/aptab_footer.jpg"></div></div>';
        if (f == null) {
            f = 300
        }
        YHD.popwin(b, f, c, null, null, d);
        if (a) {
            jQuery("#pop_win_ok_btn").click(function() {
                a()
            })
        }
        if (g) {
            jQuery("#pop_win_cancel_btn").click(function() {
                g()
            })
        }
    },confirmToLottery: function(e, a, g, f, c, d) {
        var b = "" + e + "";
        if (f == null) {
            f = 300
        }
        YHD.popwin(b, f, c, null, null, d);
        if (a) {
            jQuery("#pop_win_ok_btn").click(function() {
                a()
            })
        }
        if (g) {
            jQuery("#pop_win_cancel_btn").click(function() {
                g()
            })
        }
    },processBar: function(a, b) {
        if (a) {
            YHD.popwin('<img src="' + imagePath + '/loading.gif" />', null, null, null, null, b)
        } else {
            jQuery("#yhd_pop_win").jqmHide()
        }
    },ajax: function(b, a, e, c) {
        var f = jQuery("#yhd_pop_win");
        f.jqm({ajax: b,ajaxP: a,ajaxText: '<img src="' + imagePath + '/loading.gif" />',onLoad: e,modal: true,toTop: true,closeClass: "popwinClose"}).jqmShow();
        var d = (jQuery(window).width() - f.width()) / 2 + jQuery(window).scrollLeft() + "px";
        var g = (jQuery(window).height() - f.height()) / 2 + jQuery(window).scrollTop() + "px";
        jQuery(f).css("left", d).css("top", g)
    },ajaxPointAlert: function(b, a, e, c) {
        var f = jQuery("#yhd_pop_win");
        f.jqm({ajax: b,ajaxP: a,ajaxText: '<img src="' + imagePath + '/loading.gif" />',onLoad: e,modal: true,toTop: true,closeClass: "popwinClose"}).jqmShow();
        var d = "436.5px";
        var g = (jQuery(window).height() - f.height()) / 2 + jQuery(window).scrollTop() + "px";
        jQuery(f).css("left", d).css("top", g)
    },pageX: function(a) {
        a = a || window.event;
        return a.pageX || a.clientX + document.body.scrollLeft
    },pageY: function(a) {
        a = a || window.event;
        return a.pageY || a.clientY + document.body.scrollTop
    }};
(function(b) {
    var a = window.loli || (window.loli = {});
    a.delay = function(i, g, e, k, h) {
        var c = "";
        var j = h || 200;
        var l = j - 50;
        var f;
        b(i)[g](function() {
            var m = b(this);
            var n = true;
            if (e) {
                var n = e.call(m)
            }
            if (!(n == false)) {
                f = setTimeout(function() {
                    d.call(m)
                }, j);
                c = new Date().getTime()
            }
        });
        function d() {
            if ((new Date().getTime() - c) >= l) {
                if (k) {
                    k.call(this)
                }
                c = new Date().getTime()
            }
        }
    }
})(jQuery);
(function(r) {
    var C = ".";
    var v = {TPA: "data-tpa",TPC: "data-tpc",TPI: "data-tpi",TCS: "data-tcs",TCD: "data-tcd",TCI: "data-tci",PC: "data-pc",TP: "data-tc",TC: "data-tp",EXPR_TAG: "a,area,button",TPA_CHILD_SIZE: "data-tpaChildSize",TPC_CHILD_SIZE: "data-tpcChildSize",TC_CHILD_SIZE: "data-tcChildSize",RESULT: {RESULT: "result",TP: "tp",TC: "tc",UNIID: "uniId",PAGETYPE: "pageType",PAGEID: "pageId"}};
    var t = null, y = null, w = 0;
    var x = window.loli || (window.loli = {});
    var A = x.global.uid;
    var z = {getData: function(a) {
            p();
            if (w == -1 || w == 2) {
                return null
            }
            var b = new q(a);
            return b.getData()
        },getNewPageData: function() {
            p();
            if (w == -1) {
                return null
            }
            var a = x.util.url.getParams(location.href) || {};
            var c = a.tp;
            var b = a.tc;
            return {tp: c || "",tc: b || "",pageType: t,pageId: y,uniId: A}
        },reloadPage: function(a) {
            var b = B(window.location.href, a);
            window.location.href = b
        },refreshPage: function(b, c) {
            var a = B(b, c);
            window.location.href = a
        },openPage: function(d, g, a, e) {
            var f = B(g, d);
            var c = "";
            if (typeof (a) != "undefined" && a) {
                c = a
            }
            var b = "";
            if (typeof (e) != "undefined" && e) {
                b = e
            }
            window.open(f, c, b)
        }};
    function B(f, a) {
        if (typeof (f) == "undefined" || !f) {
            return ""
        }
        var h = typeof (a);
        if (h == "undefined" || !a) {
            return f
        }
        var e = null;
        if (h == "string") {
            var g = a;
            var k = a.indexOf("#");
            if (k == -1) {
                g = "#" + g
            }
            e = r(g)
        } else {
            if (h == "object") {
                e = a
            }
        }
        if (!e) {
            return f
        }
        var d = x.spm.getData(e);
        if (d) {
            var j = d.tp;
            var b = d.tc;
            var i = {tp: j,tc: b};
            var c = x.util.url.appendParams(f, i);
            return c
        } else {
            return f
        }
    }
    function s(a, b) {
        if (typeof (_globalSpmDataModelJson) != "undefined" && _globalSpmDataModelJson) {
            var c = 0;
            if (a) {
                c = _globalSpmDataModelJson[a][b]
            } else {
                c = _globalSpmDataModelJson[b]
            }
            if (c) {
                return c
            }
        }
        return b
    }
    function p() {
        if (!w) {
            var a = r("meta[name=tp_page]").attr("content");
            a = D(a);
            if (!a) {
                w = -1;
                return
            }
            t = s(null, a[0]);
            y = a[1];
            if (t && t == "0") {
                w = 2
            }
        }
    }
    function q(a) {
        var b = this;
        b._dom = a;
        b._opt = {};
        b.init()
    }
    q.prototype = {init: function() {
            var g = this, d = g._dom;
            if (!d) {
                g.set(v.RESULT.RESULT, 0);
                return
            }
            if (!(d instanceof r)) {
                d = r(d)
            }
            var a = d.data(v.PC);
            if (a == 1) {
                g.set(v.RESULT.RESULT, 1);
                return
            } else {
                if (a == -1) {
                    g.set(v.RESULT.RESULT, 0);
                    return
                }
            }
            var b = u(d, v.TPA);
            if (b.length < 1) {
                g.set(v.RESULT.RESULT, 0);
                return
            }
            g.set(v.TPA, b.attr(v.TPA));
            g.initTpaIndex(b);
            var e = d.data(v.TPI);
            if (!e) {
                g.initNewTpaIndex(d, b)
            }
            g.set(v.TPC, d.data(v.TPC));
            g.set(v.TPI, d.data(v.TPI));
            g.initTcdIndex(b);
            var c = u(d, v.TCS);
            var f = u(d, v.TCD);
            if (f.length > 0 && f.length > 0) {
                if (!c.attr(v.TCD)) {
                    g.initNewTcdIndex(f, b)
                }
                g.set(v.TCS, c.attr(v.TCS));
                g.set(v.TCD, f.attr(v.TCD));
                g.set(v.TCI, f.data(v.TCI) || 1)
            }
            g.set(v.RESULT.RESULT, 1)
        },rebuildTP: function(a) {
            var b = a.split(C);
            var c = s("SPM_AREA", b[2]);
            var d = s("SPM_COM", b[3]);
            return b[0] + C + b[1] + C + c + C + d + C + b[4] + C + b[5]
        },rebuildTC: function(a) {
            if (a.length == 0) {
                return a
            }
            var c = a.split(C);
            var d = s("SPM_SYSTEM_TYPE", c[0]);
            var b = s("SPM_DATA_TYPE", c[2]);
            return d + C + c[1] + C + b + C + c[3] + C + c[4]
        },getData: function() {
            var c = this, e = r(c._dom);
            var i = c.get(v.RESULT.RESULT);
            if (!i) {
                e.data(v.PC, -1);
                return null
            }
            var m = e.data(v.PC);
            if (m == 1) {
                var k = {tp: e.data(v.TP),tc: e.data(v.TC),pageType: t,pageId: y,uniId: A};
                return k
            }
            var j = c.get(v.TPA);
            var l = c.get(v.TPC);
            var d = c.get(v.TPI);
            var a = c.get(v.TCS);
            var g = c.get(v.TCD);
            var h = c.get(v.TCI);
            var b = t + C + y + C + j + C + l + C + d + C + A;
            var f = "";
            if (r.trim(a) != "" || r.trim(g) != "") {
                if (r.trim(a) != "") {
                    f += a + C
                } else {
                    f += "0.0" + C
                }
                if (r.trim(g) != "") {
                    f += g + C
                } else {
                    f += "0.0" + C
                }
                f += h
            }
            b = this.rebuildTP(b);
            f = this.rebuildTC(f);
            e.data(v.TP, b);
            e.data(v.TC, f);
            e.data(v.PC, 1);
            var k = {tp: b,tc: f,pageType: t,pageId: y,uniId: A};
            return k
        },initTpaIndex: function(i) {
            var k = i.data(v.TPA_CHILD_SIZE);
            if (k) {
                return
            }
            var f = i.find(v.EXPR_TAG);
            k = 1;
            var e = {};
            for (var c = 0, g; g = f[c]; c++) {
                g = r(g);
                var j = u(g, v.TPC);
                if (j.length < 1) {
                    g.data(v.TPI, k);
                    g.data(v.TPC, 0);
                    k++
                } else {
                    var l = j.find(v.EXPR_TAG);
                    if (l.length == 0) {
                        l = j
                    }
                    var a = j.attr(v.TPC);
                    var b = e[a] || 1;
                    for (var d = 0, h; h = l[d]; d++) {
                        r(h).data(v.TPC, a);
                        r(h).data(v.TPI, d + b)
                    }
                    e[a] = b + l.length;
                    j.data(v.TPC_CHILD_SIZE, e[a])
                }
            }
            i.data(v.TPA_CHILD_SIZE, k)
        },initNewTpaIndex: function(b, d) {
            var e = u(b, v.TPC);
            if (e.length < 1) {
                var a = d.data(v.TPA_CHILD_SIZE);
                a++;
                b.data(v.TPC, 0);
                b.data(v.TPI, a);
                d.data(v.TPA_CHILD_SIZE, a)
            } else {
                var c = e.data(v.TPC_CHILD_SIZE);
                c++;
                b.data(v.TPC, e.attr(v.TPC));
                b.data(v.TPI, c);
                e.data(v.TPC_CHILD_SIZE, c)
            }
        },initTcdIndex: function(d) {
            var e = d.data(v.TC_CHILD_SIZE);
            if (e != null) {
                return
            }
            var a = d.find("[data-tcd]");
            for (var c = 0, b; b = a[c]; c++) {
                b = r(b);
                b.data(v.TCI, c + 1)
            }
            d.data(v.TC_CHILD_SIZE, a.length)
        },initNewTcdIndex: function(c, a) {
            var b = a.data(v.TC_CHILD_SIZE);
            b++;
            a.data(v.TC_CHILD_SIZE, b);
            c.data(v.TCI, b)
        },get: function(a) {
            return this._opt[a]
        },set: function(a, b) {
            this._opt[a] = b
        }};
    function D(a) {
        if (!a) {
            return null
        }
        var b = a.split(C);
        return b.length == 2 ? b : null
    }
    function u(b, a) {
        return b.closest("[" + a + "]")
    }
    x.spm = z
})(jQuery);
Array.prototype.toTRACKERJSONString = function() {
    var a = "[";
    for (var b = 0; b < this.length; b++) {
        if (this[b] instanceof Parameter) {
            if (this[b].value instanceof Array) {
                a += "{" + this[b].key + "=" + this[b].value.toTRACKERJSONString() + "},"
            } else {
                a += this[b].toJSONString() + ","
            }
        }
    }
    if (a.indexOf(",") > 0) {
        a = a.substring(0, a.length - 1)
    }
    return a + "]"
};
var trackerUrl = "";
function Parameter(a, b) {
    this.key = a;
    if (this.key == "internalKeyword") {
        this.value = encodeURI(b)
    } else {
        this.value = b
    }
    this.toJSONString = function() {
        return "{" + this.key + "=" + this.value + "}"
    }
}
var linkPosition = "";
var buttonPosition = "";
function TrackerContainer(a) {
    this.url = a;
    this.parameterArray = new Array();
    this.stockArray = new Array();
    this.commonAttached = new Array();
    this.addParameter = function(b) {
        this.parameterArray.push(b)
    };
    this.addStock = function(c, b) {
        this.stockArray.push(new Parameter(c, b))
    };
    this.addCommonAttached = function(b, c) {
        this.commonAttached.push(new Parameter(b, c))
    };
    this.buildAttached = function() {
        if (this.stockArray.length > 0) {
            this.commonAttached.push(new Parameter("1", this.stockArray))
        }
        if (this.commonAttached.length > 0) {
            this.addParameter(new Parameter("attachedInfo", this.commonAttached.toTRACKERJSONString("attachedInfo")))
        }
    };
    this.toUrl = function() {
        this.buildAttached();
        for (var c = 0; c < this.parameterArray.length; c++) {
            var b = this.parameterArray[c].key;
            var d = this.parameterArray[c].value;
            this.url += "&" + b + "=" + d
        }
        trackerUrl = this.url;
        return this.url
    }
}
var trackerUrl = ("https:" == document.location.protocol ? "https://" : "http://") + URLPrefix.tracker + "/tracker/info.do?1=1";
ilog('trackerUrl.1:'+trackerUrl);
var trackerContainer = new TrackerContainer(trackerUrl);
var ieVersion = navigator.userAgent || "";
var platform = navigator.platform || "";
trackerContainer.addParameter(new Parameter("ieVersion", ieVersion));
trackerContainer.addParameter(new Parameter("platform", platform));

ilog('trackerUrl.2:'+trackerContainer.toUrl());
var page_refer = document.referrer ? document.referrer.substring(0, document.referrer.lastIndexOf("/")) : "";
var page_location = window.location.host;
function addTrackPositionToCookie(b, a) {
    document.cookie = "linkPosition=" + encodeURIComponent(a) + ";path=/;domain=." + no3wUrl + ";"
}
function addPageMsgToCookie(a) {
    if (typeof (a) == "object" && a) {
        if (typeof (a.pmInfoId) != "undefined") {
            document.cookie = "pmInfoId=" + encodeURIComponent(a.pmInfoId) + ";path=/;domain=." + no3wUrl + ";"
        }
        if (typeof (a.productId) != "undefined") {
            document.cookie = "productId=" + encodeURIComponent(a.productId) + ";path=/;domain=." + no3wUrl + ";"
        }
    }
}
function getCookie(a) {
    var c = document.cookie;
    var d = c.split("; ");
    for (var b = 0; b < d.length; 
    b++) {
        var e = d[b].split("=");
        if (e[0] == a) {
            return e[1]
        }
    }
    return null
}
var e1 = new RegExp("exfield1=[^;]*;*", "i");
var e2 = new RegExp("exfield2=[^;]*;*", "i");
var e3 = new RegExp("exfield3=[^;]*;*", "i");
var e4 = new RegExp("exfield4=[^;]*;*", "i");
var e5 = new RegExp("exfield5=[^;]*;*", "i");
function recordTrackInfoWithType(n, h, f, m) {
    var d = ("https:" == document.location.protocol ? "https://" : "http://") + URLPrefix.tracker + "/related/info.do?1=1";
    var j = {};
    if (n && h) {
        j.infoType = n;
        j.relatedInfo = encodeURIComponent(h) || "";
        j.attachedInfo = encodeURIComponent(f) || "";
        if (document) {
            j.url = document.URL || "";
            j.infoPreviousUrl = document.referrer || ""
        }
        j.ieVersion = ieVersion;
        j.platform = platform;
        if (m) {
            var g = e1.exec(m);
            if (g) {
                j.exField1 = g[0].replace(/exfield1=/i, "").replace(";", "")
            }
            var e = e2.exec(m);
            if (e) {
                j.exField2 = e[0].replace(/exfield2=/i, "").replace(";", "")
            }
            var c = e3.exec(m);
            if (c) {
                j.exField3 = c[0].replace(/exfield3=/i, "").replace(";", "")
            }
            var b = e4.exec(m);
            if (b) {
                j.exField4 = b[0].replace(/exfield4=/i, "").replace(";", "")
            }
            var a = e5.exec(m);
            if (a) {
                j.exField5 = a[0].replace(/exfield5=/i, "").replace(";", "")
            }
        }
        for (var l in j) {
            d += "&" + l + "=" + encodeURIComponent(j[l])
        }
        var k = new Image(1, 1);
        k.src = d
    }
}
function gotracker(b, j, g, a) {
    var e = trackerUrl;
    var f = new RegExp("&linkPosition=\\w*", "g");
    e = e.replace(f, "");
    var f = new RegExp("&buttonPosition=\\w*", "g");
    e = e.replace(f, "");
    var f = new RegExp("&productId=\\w*", "g");
    e = e.replace(f, "");
    var f = new RegExp("&extField7=\\w*", "g");
    e = e.replace(f, "");
    var f = new RegExp("&extField8=\\w*", "g");
    e = e.replace(f, "");
    var f = new RegExp("&infoModuleId=[^&]*", "g");
    e = e.replace(f, "");
    var f = new RegExp("&infoLinkId=[^&]*", "g");
    e = e.replace(f, "");
    var f = new RegExp("&infoPageId=[^&]*", "g");
    e = e.replace(f, "");
    var f = new RegExp("&edmEmail=[^&]*", "g");
    e = e.replace(f, "");
    if (j != null) {
        e += "&buttonPosition=" + j
    }
    if (g != null) {
        e += "&productId=" + g
    }
    if (typeof (b) == "number" && (b > 2 || b < 0)) {
        e += "&extField7=" + b
    } else {
        if (typeof (b) == "string") {
            var i = Number(b);
            if (i > 2 || i < 0) {
                e += "&extField7=" + i
            }
        }
    }
    if (typeof (a) == "object" && a) {
        var h = a.tc;
        var c = a.tp;
        e += "&infoModuleId=" + c;
        e += "&infoLinkId=" + h
    }
    var d = getCookie("edmEmail");
    if (d != null) {
        e += "&edmEmail=" + d
    }
    ilog('trackerUrl:'+e);
    jQuery.ajax({async: true,url: e,type: "GET",dataType: "jsonp",jsonp: "jsoncallback"})
}
function bindLinkClickTracker(a, c) {
    var b = jQuery("#" + a + " a");
    b.click(function() {
        var d = jQuery(this).text();
        d = c + "_" + encodeURIComponent(jQuery.trim(d));
        addTrackPositionToCookie("1", d)
    })
}
function callLoadCookie() {
}
function callLoadlinkCookie() {
}
function callAddCookieApi(a, b) {
}
;
(function(b) {
    var a = (function() {
        var i = 300;
        var d = function() {
        };
        var j = {rowSelector: "> li",submenuSelector: "*",submenuDirection: "right",tolerance: 75,over: d,out: d,active: d,deactive: d,exit: d};
        var h = [], c = null, f = null;
        var g = false;
        var e = function(l, k) {
            return (k.y - l.y) / (k.x - l.x)
        };
        return function(k) {
            var o = b(this);
            var p = b.extend(j, k);
            var q = null;
            var l = function() {
                if (this == q) {
                    return
                }
                if (q) {
                    p.deactive.call(q)
                }
                p.active.call(this);
                q = this
            };
            var n = function(s) {
                var r = m();
                if (r) {
                    f = setTimeout(function() {
                        l.call(s)
                    }, r)
                } else {
                    l.call(s)
                }
            };
            var m = function() {
                if (!q || !b(q).is(p.submenuSelector)) {
                    return 200
                }
                var v = o.offset(), D = {x: v.left,y: v.top - p.tolerance}, B = {x: v.left + o.outerWidth(),y: D.y}, r = {x: v.left,y: v.top + o.outerHeight() + p.tolerance}, w = {x: v.left + o.outerWidth(),y: r.y}, x = h[h.length - 1], A = h[0];
                if (!x) {
                    return 0
                }
                if (!A) {
                    A = x
                }
                if (A.x < v.left || A.x > w.x || A.y < v.top || A.y > w.y) {
                    return 0
                }
                if (c && x.x == c.x && x.y == c.y) {
                    return 0
                }
                var z = B, s = w;
                if (p.submenuDirection == "left") {
                    z = r;
                    s = D
                } else {
                    if (p.submenuDirection == "below") {
                        z = w;
                        s = r
                    } else {
                        if (p.submenuDirection == "above") {
                            z = D;
                            s = B
                        }
                    }
                }
                var t = e(x, z), y = e(x, s), C = e(A, z), u = e(A, s);
                if (t < C && y > u) {
                    c = x;
                    return i
                }
                c = null;
                return 0
            };
            g === false && b(document).bind("mousemove.initMenu", function(r) {
                h.push({x: r.pageX,y: r.pageY});
                if (h.length > 3) {
                    h.shift()
                }
            });
            o.bind("mouseleave.initMenu", function() {
                f && clearTimeout(f);
                if (p.exit.call(this) === true) {
                    if (q) {
                        p.deactive.call(q)
                    }
                    q = null
                }
            }).find(p.rowSelector).bind("mouseenter.initMenu", function() {
                f && clearTimeout(f);
                p.over.call(this);
                n(this)
            }).bind("mouseleave.initMenu", function() {
                p.out.call(this)
            }).bind("click.initMenu", function() {
                l.call(this)
            })
        }
    })();
    b.fn.yhdMenu = function(c) {
        return this.each(function() {
            a.call(this, c)
        })
    }
})(jQuery);
(function(g) {
    var e = jQuery("#allCategoryHeader");
    function p(x, u) {
        var r = x;
        var z = r.data("data-flag");
        if (z == 1) {
            return
        }
        var v = r.find("div[categoryId]");
        var y = v.attr("categoryId");
        var t = v.attr("cindex");
        r.data("data-flag", 1);
        var s = "GLOBALLEFTMENU_" + y;
        var w = jQuery.cookie("provinceId");
        var q = typeof (currProvinceId) != "undefined" ? currProvinceId : (w ? w : 1);
        u = u + "?categoryId=" + y + "&cindex=" + t + "&leftMenuProvinceId=" + q + "&isFixTopNav=" + isFixTopNav + "&callback=" + s;
        window[s] = function(A) {
            v.append(A.value);
            var B = r.find(".hd_show_sort");
            if (r.hasClass("cur")) {
                B.show();
                b(r)
            }
            B.removeClass("global_loading");
            l(v);
            h(r, v);
            window[s] = null
        };
        jQuery.getScript(u)
    }
    function f(r) {
        var q = e.height();
        r.css("min-height", q - 3);
        r.css("height", q - 1)
    }
    function d() {
        if (g.browser.msie && (g.browser.version == "6.0")) {
            var r = g(".hd_show_sort").height();
            var q = g(".allsort_ifm").length;
            if (q == 0) {
                g("<iframe class=allsort_ifm></iframe>").insertBefore(".allsort_out_box .allsort_out");
                if (r > 481) {
                    g(".allsort_ifm").height(r)
                } else {
                    g(".allsort_ifm").height(481)
                }
            }
        }
    }
    function j() {
        if (g.browser.msie && (g.browser.version == "6.0")) {
            g(".allsort_ifm").remove()
        }
    }
    function c() {
        var q = (isIndex == 1 && (typeof (indexFlag) != "undefined" && typeof (indexFlag) == "number" && indexFlag == 1));
        if (!q && typeof isMallIndex != "undefined" && isMallIndex == 1) {
            q = 1
        }
        if (!q) {
            var r;
            jQuery("#allSortOuterbox").hover(function() {
                if (r) {
                    clearTimeout(r)
                }
                r = setTimeout(function() {
                    jQuery("#allSortOuterbox").children(".hd_allsort_out_box").show();
                    jQuery("#allSortOuterbox").addClass("hover")
                }, 200)
            }, function() {
                if (r) {
                    clearTimeout(r)
                }
                r = setTimeout(function() {
                    jQuery("#allSortOuterbox li.cur").removeClass("cur").children(".hd_show_sort").hide();
                    jQuery("#allSortOuterbox").children(".hd_allsort_out_box").hide();
                    jQuery("#allSortOuterbox").removeClass("hover")
                }, 200)
            })
        }
    }
    function b(v) {
        if (!g("#headerNav").hasClass("hd_nav_fixed")) {
            var x = g("#allCategoryHeader").offset().top;
            var w = v.offset().top - x;
            var u = v.find(".hd_show_sort");
            var t = document.documentElement.scrollTop || document.body.scrollTop;
            var r = w + u.height() + x - t;
            var q = g(window).height() - 30;
            var s = r - q;
            if (r > q) {
                if (v.offset().top - t + v.height() - q > -10) {
                    w = v.position().top - u.height() + v.height() - 2
                } else {
                    w = w - s - 10
                }
            }
            if (u.height() > q) {
                w = t - x
            }
            u.css({top: w})
        } else {
            v.find(".hd_show_sort").css({top: "0px"})
        }
    }
    function i(r) {
        var q = window.loli || (window.loli = {});
        var s = q.yhdStore;
        if (s) {
            s.getFromRoot("category_history", function(z) {
                if (z && z.status == 1) {
                    var y = z.value;
                    var x = [];
                    if (y) {
                        var u = y.split(",");
                        for (var A = 0; A < u.length; A++) {
                            var t = u[A];
                            if (t) {
                                var B = t.split("~");
                                var C = B[0];
                                var w = decodeURIComponent(B[1]);
                                var v = decodeURIComponent(B[2]);
                                x.push({cateId: C,cateName: w,cateUrl: v})
                            }
                        }
                    }
                    if (typeof r == "function") {
                        r(x)
                    }
                }
            })
        }
    }
    function a(q) {
        var s = [];
        if (q && q.length > 0) {
            s.push("<div class='hd_sort_history clearfix'>历史记录");
            for (var r = q.length - 1; r >= 0; r--) {
                s.push("<a href='" + q[r].cateUrl + "' target='_blank' data-ref='YHD_GLOBAL_CatMenu_History_" + q[r].cateId + "'>" + q[r].cateName + "</a>")
            }
            s.push("<a href='javascript:void(0);' tk='YHD_GLOBAL_CatMenu_DeleteHistory' class='hd_clear_history'>清除记录</a>");
            s.push("</div>")
        }
        return s.join("")
    }
    function o() {
        g("#allCategoryHeader").delegate("div.hd_sort_list a", "click", function() {
            var s = g(this);
            var u = s.text();
            var r = s.attr("href");
            var t = s.attr("categoryId");
            var q = window.loli || (window.loli = {});
            var v = q.yhdStore;
            if (v) {
                v.getFromRoot("category_history", function(H) {
                    if (H && H.status == 1) {
                        var G = H.value;
                        var D = [];
                        if (G) {
                            D = G.split(",");
                            var z = false;
                            var y = 0;
                            for (var w = 0; w < D.length; w++) {
                                var C = D[w];
                                if (C) {
                                    var x = C.split("~");
                                    var B = x[0];
                                    var F = decodeURIComponent(x[1]);
                                    var E = decodeURIComponent(x[2]);
                                    if (t == B) {
                                        z = true;
                                        y = w;
                                        break
                                    }
                                }
                            }
                            if (!z) {
                                D.push(t + "~" + encodeURIComponent(u) + "~" + encodeURIComponent(r));
                                if (D.length > 10) {
                                    D.shift()
                                }
                            } else {
                                if (y != D.length - 1) {
                                    var A = D.splice(y, 1);
                                    D.push(A[0])
                                }
                            }
                        } else {
                            D.push(t + "~" + encodeURIComponent(u) + "~" + encodeURIComponent(r))
                        }
                        v.setFromRoot("category_history", D.join(","), k)
                    }
                })
            }
        });
        g("#allCategoryHeader").delegate("div.hd_sort_history a.hd_clear_history", "click", function() {
            var q = g(this).tk;
            gotracker(2, q);
            n()
        })
    }
    function n() {
        var q = window.loli || (window.loli = {});
        var r = q.yhdStore;
        if (r) {
            r.setFromRoot("category_history", "")
        }
        g("#allCategoryHeader div.hd_sort_history").remove()
    }
    function k() {
        var q = function(s) {
            var r = a(s);
            if (r.length > 0) {
                g("#allCategoryHeader div.hd_sort_history").remove();
                g("#allCategoryHeader div.hd_sort_list_wrap").append(r)
            }
        };
        i(q)
    }
    function l(r) {
        var q = function(s) {
            var t = a(s);
            if (t.length > 0) {
                g("div.hd_sort_list_wrap", r).append(t)
            }
        };
        i(q)
    }
    function h(A, t) {
        var C = (typeof isIndex != "undefined" && isIndex == 1) && (typeof indexFlag != "undefined" && indexFlag == 1);
        if (!C) {
            return
        }
        var w = (typeof isWidescreen != "undefined" && isWidescreen == true) ? true : false;
        if (!w) {
            return
        }
        var v = g.cookie("abtest");
        var u = typeof homepageCategoryBackgroundRate != "undefined" ? homepageCategoryBackgroundRate : "";
        if (u == "" || !v || isNaN(v) || parseInt(v) < 0 || parseInt(v) >= 100) {
            return
        }
        var q = function(F) {
            var K = 0;
            var J = u.split(",");
            for (var I = 0; I < J.length; I++) {
                var H = J[I].split(":");
                var G = H[0].substring(1, H[0].length - 1).split("-")[0];
                var E = H[0].substring(1, H[0].length - 1).split("-")[1];
                var L = H[1];
                if (!isNaN(F) && parseInt(F) >= parseInt(G) && parseInt(F) <= parseInt(E)) {
                    K = L;
                    break
                }
            }
            return K
        };
        var r = q(v);
        if (r > 1) {
            var x = A.attr("data-background");
            if (x) {
                var B = x.split("^");
                var y = B.length > 0 ? B[0] : "";
                var s = B.length > 1 ? B[1] : "";
                var z = B.length > 2 ? B[2] : "";
                if (y != "") {
                    t.addClass("hd_has_pic");
                    var D = [];
                    D.push("<a class='hd_allsort_pic' href='" + (s != "" ? s : "javascript:void(0);") + "'" + (s != "" ? " target='_blank' data-ref='" + z + "'" : " style='cursor:default;'") + ">");
                    D.push("<img src='" + y + "'>");
                    D.push("</a>");
                    t.prepend(D.join(""))
                }
            }
        }
    }
    function m() {
        var q = window.navigator.userAgent;
        var r = /(iPad|pad)/i;
        if (!r.test(q)) {
            return
        }
        jQuery("#j_allsort li").delegate("a", "click", function() {
            var u = jQuery(this);
            var t = u.closest("li");
            if (t.hasClass("cur")) {
                return true
            } else {
                return false
            }
        });
        var s = jQuery("#allSortOuterbox");
        if (s.hasClass("not_index")) {
            s.delegate(".hd_all_sort_link a", "click", function() {
                if (s.hasClass("hover")) {
                    jQuery("#allSortOuterbox li.cur").removeClass("cur").children(".hd_show_sort").hide();
                    s.children(".hd_allsort_out_box").hide();
                    s.removeClass("hover")
                } else {
                    s.children(".hd_allsort_out_box").show();
                    s.addClass("hover")
                }
                return false
            })
        }
    }
    g(document).ready(function() {
        m();
        c();
        var r = currDomain + "/header/ajaxGetGlobalLeftFloatMenuDataV10.do";
        var q = g("#j_allsort");
        q.yhdMenu({active: function() {
                var u = g(this);
                var t = g(this).attr("data-color");
                u.addClass("cur").addClass(t + "_cur");
                p(u, r);
                d();
                b(u)
            },deactive: function() {
                var t = g(this).attr("data-color");
                g(this).removeClass("cur").removeClass(t + "_cur");
                j()
            },exit: function() {
                return true
            }});
        function s() {
            var z, y, x, v, t, w, u;
            g("#allCategoryHeader").delegate("div.hd_show_sort .hd_good_category", "mouseenter", function() {
                var B = g(this);
                z = B.parents(".hd_show_sort");
                w = z.width();
                t = B.attr("data-info");
                y = g(this).position().left + g(this).outerWidth();
                x = g(this).position().top - 10;
                g(".hd_good_category_hover span", z).text(t);
                u = g(".hd_good_category_hover", z).width();
                if (g.browser.msie && (g.browser.version == "6.0") || false) {
                    var C = 286;
                    if (u > C) {
                        g(".hd_good_category_hover", z).width(C);
                        u = C
                    } else {
                        g(".hd_good_category_hover", z).width("auto")
                    }
                }
                if (u > w - y) {
                    var A = w - y + g(this).outerWidth();
                    g(".hd_good_category_hover", z).show().css({left: "auto",right: A,top: x});
                    g(".hd_good_category_hover b", z).css({left: "auto",right: "-1px","background-position": "0 -410px"})
                } else {
                    g(".hd_good_category_hover", z).show().css({left: y,right: "auto",top: x});
                    g(".hd_good_category_hover b", z).css({left: "-1px",right: "auto","background-position": "0 -400px"})
                }
            });
            g("#allCategoryHeader").delegate("div.hd_show_sort .hd_good_category", "mouseleave", function() {
                g(".hd_good_category_hover", z).hide()
            })
        }
        s();
        o()
    })
})(jQuery);
(function() {
    if ($.fn.bgiframe) {
        return false
    }
    var b = "";
    if (URLPrefix && URLPrefix.statics) {
        b = URLPrefix.statics
    } else {
        if (currSiteId && currSiteId == 2) {
            b = "http://image.111.com.cn/statics"
        } else {
            b = "http://image.yihaodianimg.com/statics"
        }
    }
    var a = document.createElement("script");
    a.setAttribute("type", "text/javascript");
    a.setAttribute("src", b + "/global/js/libs/jquery/jquery.bgiframe.js?" + currVersionNum);
    document.getElementsByTagName("head")[0].appendChild(a)
})();
var yhdLib = yhdLib || {};
if (!yhdLib.hasOwnProperty("popwin")) {
    yhdLib.popwin = function(param) {
        var arg = param, tcBox = ".popGeneral", sFun = arg.fun ? arg.fun : [], cTxt = arg.popcontentstr ? arg.popcontentstr : "", popEvent = arg.popevent ? arg.popevent : "click", autoClose = arg.autoclosetime;
        var fixed = typeof (arg.fix) == "undefined" || arg.fix ? true : false;
        if (arg.clickid) {
            $(arg.clickid).bind(popEvent, function() {
                if ($(".popGeneral").length == 0) {
                    popMask()
                }
            })
        } else {
            if ($(".popGeneral").length == 0) {
                popMask()
            }
        }
        function popMask() {
            var dwidth = "100%", dheight = $(document).height();
            if ($.browser.msie && $.browser.version == 6) {
                $("select:visible", ".delivery").each(function(i) {
                    $(this).addClass("selectSjl").hide()
                })
            }
            var popBOX = !fixed ? '<div class="popGeneral" style="position:absolute;" ' : '<div class="popGeneral" ';
            if (arg.poptitle) {
                popBOX += '><div class="top_tcgeneral"><h4>' + arg.poptitle + '</h4><span class="close_tcg">关闭</span></div></div>'
            } else {
                popBOX += "></div>"
            }
            if (arg.mask || arg.mask == null) {
                $('<div class="mask_tcdiv"></div>').appendTo($("body")).css({position: "absolute",top: 0,right: 0,bottom: 0,left: 0,zIndex: 100001,width: dwidth + "",height: dheight + "px",background: "#000",opacity: 0.4})
            }
            $(popBOX).appendTo($("body"));
            $(".mask_tcdiv").bgiframe();
            loli.scroll(function() {
                $(".mask_tcdiv").height($(document).height())
            });
            if (arg.popwidth) {
                $(".popGeneral").width(arg.popwidth)
            }
            if (arg.popheight) {
                $(".popGeneral").height(arg.popheight)
            }
            var apTxt = cTxt ? $(cTxt) : $(arg.popcontent).clone();
            apTxt.appendTo($(tcBox)).show();
            popPosition();
            for (var funI = sFun.length - 1; funI >= 0; funI--) {
                eval(sFun[funI] + "()")
            }
            return false
        }
        function popPosition() {
            var popwinTop = 0;
            $(window).resize(function() {
                var width = $(tcBox).width(), height = $(tcBox).height() / 2, windWidth = $(window).width(), pLeft = (windWidth - width) / 2;
                $(tcBox).css({left: pLeft,top: "50%",bottom: "auto",marginTop: "-" + height + "px"});
                popwinTop = $(window).height() / 2 - height
            }).trigger("resize");
            if ($.browser.msie && $.browser.version == 6 && fixed) {
                $(window).scroll(function() {
                    $(tcBox).css({top: popwinTop + $(window).scrollTop() + "px",marginTop: 0})
                }).trigger("scroll")
            }
            $(".close_tcg").click(function() {
                closeTc()
            });
            if (autoClose) {
                setTimeout(function() {
                    closeTc()
                }, autoClose)
            }
            if (arg.outareaclose) {
                $(".mask_tcdiv").click(function() {
                    closeTc()
                })
            }
            $(window).keydown(function(event) {
                if (event.keyCode == 27) {
                    closeTc()
                }
            });
            return false
        }
        function closeTc() {
            $(".popGeneral").remove();
            $(".mask_tcdiv").remove();
            if ($.browser.msie && $.browser.version == 6) {
                $("select.selectSjl").each(function() {
                    $(this).removeClass("selectSjl").show()
                })
            }
        }
        return false
    }
}
if (!yhdLib.hasOwnProperty("popclose")) {
    yhdLib.popclose = function() {
        if ($.browser.msie && $.browser.version == 6) {
            $("select.selectSjl").each(function() {
                $(this).removeClass("selectSjl").show()
            })
        }
        $(".popGeneral,.mask_tcdiv").remove()
    }
}
if (!yhdLib.hasOwnProperty("popwinreload")) {
    yhdLib.popwinreload = function() {
        if ($("body > .popGeneral").length) {
            $(window).trigger("resize")
        }
    }
}
if (!yhdLib.hasOwnProperty("ratebox")) {
    yhdLib.ratebox = function(rateboxArgus) {
        var rateArg = rateboxArgus, rateObj = document.getElementById(rateArg.id), rateDg = rateArg.ratedegree;
        if (rateArg.autorate) {
            var rtim = rateArg.ratetime ? rateArg.ratetime : 15, step = rateArg.step ? rateArg.step : 20;
            if (rateDg >= 0) {
                setInterval(function() {
                    rate(rateObj, (rateDg += step) >= 360 ? rateDg = 0 : rateDg);
                    return false
                }, rtim)
            } else {
                if (rateDg < 0) {
                    setInterval(function() {
                        rate(rateObj, (rateDg -= step) <= 0 ? rateDg = 360 : rateDg);
                        return false
                    }, rtim)
                }
            }
        } else {
            rate(rateObj, rateDg)
        }
        function rate(obj, degree) {
            var ST = obj.style;
            if (document.all) {
                var deg = degree * Math.PI / 180, M11 = Math.cos(deg), M12 = -Math.sin(deg), M21 = Math.sin(deg), M22 = Math.cos(deg);
                obj.fw = obj.fw || obj.offsetWidth / 2;
                obj.fh = obj.fh || obj.offsetHeight / 2;
                var adr = (90 - degree % 90) * Math.PI / 180, adp = Math.sin(adr) + Math.cos(adr);
                with (ST) {
                    filter = "progid:DXImageTransform.Microsoft.Matrix(M11=" + M11 + ",M12=" + M12 + ",M21=" + M21 + ",M22=" + M22 + ",SizingMethod='auto expand');";
                    top = obj.fh * (1 - adp) + "px";
                    left = obj.fw * (1 - adp) + "px"
                }
            } else {
                var rotate = "rotate(" + degree + "deg)";
                with (ST) {
                    MozTransform = rotate;
                    WebkitTransform = rotate;
                    OTransform = rotate;
                    Transform = rotate
                }
            }
            return false
        }
        return false
    }
}
jQuery.yhdtool = yhdLib;
(function(b) {
    b(function() {
        var d = function() {
            var h = (typeof hideGlobalCookieCheckMsgFlag != "undefined" && hideGlobalCookieCheckMsgFlag == "1") ? 1 : 0;
            if (h) {
                return
            }
            var c = "提示消息";
            var g = [];
            g.push("<div>");
            g.push("<style>");
            g.push(".no_cookie {height:150px;width:500px;text-align:center;padding:20px;font-size:20px;}");
            g.push(".no_cookie a:link,.no_cookie a:visited {color:blue; text-decoration: none;}");
            g.push(".no_cookie a:hover,.no_cookie a:active {color:blue; text-decoration: underline;}");
            g.push("</style>");
            g.push("<div class='no_cookie'>由于您使用的浏览器设置问题可能导致网页运行不正常。请您启用浏览器Cookie功能或更换浏览器。<br/><a href='http://cms.yhd.com/cms/view.do?topicId=24243' target='_blank'>如何启用Cookie？</a></div>");
            g.push("</div>");
            yhdLib.popwin({poptitle: c,popcontentstr: g.join("")})
        };
        if (!window.navigator.cookieEnabled) {
            d()
        } else {
            jQuery.cookie("test_cookie", "1");
            if (jQuery.cookie("test_cookie")) {
                var a = new Date();
                a.setTime(a.getTime() - 10000);
                document.cookie = "test_cookie=;path=;domain=;expires=" + a.toGMTString()
            } else {
                d()
            }
        }
    })
})(jQuery);
var YHDPROVINCE = {};
YHDPROVINCE.getCurentDomain = function() {
    return URLPrefix.central
};
YHDPROVINCE.getOppositeDomain = function() {
    return URLPrefix.central
};
YHDPROVINCE.proviceObj = {p_1: "上海",p_2: "北京",p_3: "天津",p_4: "河北",p_5: "江苏",p_6: "浙江",p_7: "重庆",p_8: "内蒙古",p_9: "辽宁",p_10: "吉林",p_11: "黑龙江",p_12: "四川",p_13: "安徽",p_14: "福建",p_15: "江西",p_16: "山东",p_17: "河南",p_18: "湖北",p_19: "湖南",p_20: "广东",p_21: "广西",p_22: "海南",p_23: "贵州",p_24: "云南",p_25: "西藏",p_26: "陕西",p_27: "甘肃",p_28: "青海",p_29: "新疆",p_30: "宁夏",p_32: "山西"};
YHDPROVINCE.swithAddressCity = function(b, a) {
    provinceSwitchProvince(b, oldProvinceId, paramObj)
};
function setAddressCity(d, b) {
    var a = jQuery.cookie("provinceId");
    var c = {};
    if (b) {
        c.targetUrl = b
    }
    jQuery.cookie("provinceId", d, {domain: no3wUrl,path: "/",expires: 800});
    glaCookieHandler.genGlaCookie({provinceId: d});
    provinceSwitchProvince(d, a, c)
}
function provinceSwitchProvince(b, c, a) {
    moveCartItem(b, c, a)
}
function setAddressCityback(j) {
    var r = null;
    if (j && j.targetUrl) {
        r = j.targetUrl;
        window.location.href = r;
        return
    }
    if (currSiteId == 2) {
        addTrackPositionToCookie("1", "YW_Province")
    }
    var b = window.location.href;
    if (b.indexOf("merchantID=") != -1) {
        b = b.substring(0, b.indexOf("merchantID=") - 1);
        window.location.href = b;
        return
    }
    if (b.indexOf("merchant=") != -1) {
        b = b.substring(0, b.indexOf("merchant=") - 1);
        window.location.href = b;
        return
    }
    if (b.indexOf("/tuangou/") != -1) {
        if (b.indexOf("/tuangou/myGroupon.do") != -1) {
            window.location.href = b
        }
        return
    }
    if (b.indexOf("openProvincePage=") != -1) {
        b = b.substring(0, b.indexOf("openProvincePage=") - 1);
        window.location.href = b;
        return
    }
    if (b.indexOf("/cart/cart.do?action=view") != -1) {
        window.location.href = "/cart/cart.do?action=view";
        return
    }
    var o = /^\S*product\/\d+_?\d+/;
    if (b.match(o)) {
        if (b.indexOf("_") != -1) {
            b = b.substring(0, b.indexOf("_"))
        } else {
            if (b.indexOf("#") != -1) {
                var p = b.indexOf("#");
                b = b.substring(0, p)
            }
        }
        window.location.href = b;
        return
    }
    var h = /^(http:\/\/){0,1}([^\/]+\/)[0-9]+\/([^\/]*)$/;
    if (b.match(h)) {
        b = b.replace(h, "$1$2$3");
        window.location.href = b;
        return
    }
    var i = /^(http:\/\/){0,1}([^\/]+\/)([^\/]*)$/;
    if (b.match(i)) {
        window.location.href = b;
        return
    }
    var s = /^(http:\/\/){0,1}[^\/]+\/channel\/[0-9]+_[0-9]+\/$/;
    if (b.match(s)) {
        b = b.substring(0, b.lastIndexOf("_"));
        window.location.href = b;
        return
    }
    var a = /^(http:\/\/){0,1}[^\/]+\/cms\/view.do\?topicId=[0-9]+&merchant=[0-9]+$/;
    if (b.match(a)) {
        b = b.substring(0, b.lastIndexOf("&merchant"));
        window.location.href = b;
        return
    }
    var n = /^(http:\/\/){0,1}[^\/]+\/brand\/[0-9]+\/{0,1}(\?[^\/]+)*$/;
    if (b.match(n)) {
        window.location.href = b;
        return
    }
    var g = /^(http:\/\/){0,1}[^\/]+\/try\/[0-9]+\/{0,1}(\?[^\/]+)*$/;
    if (b.match(g)) {
        if (b.lastIndexOf("/") == b.length - 1) {
            b = b.substring(0, b.lastIndexOf("/"))
        }
        b = b.substring(0, b.lastIndexOf("/"));
        window.location.href = b;
        return
    }
    var c = /^(http:\/\/){0,1}[^\/]+\/try\/[0-9]+_[0-9]+\/{0,1}(\?[^\/]+)*$/;
    if (b.match(c)) {
        b = b.substring(0, b.lastIndexOf("_")) + "_0/";
        window.location.href = b;
        return
    }
    var l = /^(http:\/\/){0,1}[^\/]+\/S-theme\/[0-9]+\/{0,1}(\?[^\/]+)*$/;
    if (b.match(l)) {
        window.location.href = b;
        return
    }
    var f = /^(http:\/\/){0,1}[^\/]+\/ctg\/s2\/c([0-9]*)-([^?^\/]*)\/([0-9]*)\/$/;
    if (b.match(f)) {
        if (b.lastIndexOf("/") == b.length - 1) {
            b = b.substring(0, b.lastIndexOf("/"))
        }
        b = b.substring(0, b.lastIndexOf("/") + 1);
        window.location.href = b;
        return
    }
    var e = /^(http:\/\/){0,1}search.[^\/]+\/s2\/c([0-9]*)-([^?^\/]*)\/k([^?^\/]*)\/([0-9]*)\/$/;
    if (b.match(e)) {
        if (b.lastIndexOf("/") == b.length - 1) {
            b = b.substring(0, b.lastIndexOf("/"))
        }
        b = b.substring(0, b.lastIndexOf("/") + 1);
        window.location.href = b;
        return
    }
    var m = /^(http:\/\/){0,1}channel\.[^\/]+\/[^\/^_^\.]+(\/[^\/^\.]+){0,1}\/[0-9]+\/{0,1}(\?[^\/]+){0,1}(#[^\/]+)*$/;
    if (b.match(m)) {
        if (b.indexOf("#") != -1) {
            b = b.substring(0, b.indexOf("#"))
        }
        if (b.indexOf("?") != -1) {
            var k = b.substring(b.indexOf("?"));
            var q = b.substring(0, b.indexOf("?"));
            if (b.lastIndexOf("/") == b.length - 1) {
                q = q.substring(0, q.lastIndexOf("/"));
                k = "/" + k
            }
            q = q.substring(0, q.lastIndexOf("/"));
            b = q + k
        } else {
            if (b.lastIndexOf("/") == b.length - 1) {
                b = b.substring(0, b.lastIndexOf("/"))
            }
            b = b.substring(0, b.lastIndexOf("/"))
        }
        window.location.href = b;
        return
    }
    if (b.indexOf("confirmOrder") != -1 && b.indexOf("saveOrder") != -1) {
        window.location.href = YHDPROVINCE.getCurentDomain();
        return
    }
    var d = URLPrefix.search + "/s/";
    if (b.substr(0, d.length) == d) {
        var o = /-p\d{0,3}/;
        if (b.match(o)) {
            b = b.replace(o, "-p1");
            window.location.href = b;
            return
        }
    }
    loli.spm.reloadPage(jQuery("#currProvince"))
}
function moveCartItem(a, f, e) {
    var h = 1;
    var d = {};
    var g = {};
    var c = [];
    if (typeof e != "undefined" && e) {
        if (typeof e.isSetAddress != "undefined" && e.isSetAddress) {
            if (e.isSetAddress == 0) {
                h = e.isSetAddress
            }
        }
        if (typeof e.callback != "undefined" && e.callback) {
            d = e.callback;
            if (typeof d.func != "undefined" && d.func) {
                g = d.func
            }
            if (typeof d.args != "undefined" && d.func) {
                c = d.args
            }
        }
    }
    var b = YHDPROVINCE.getCurentDomain();
    jQuery.getJSON(b + "/cart/globalMoveCartItem.do?provinceId=" + a + ((f) ? "&oldProvinceId=" + f : "") + "&timestamp=" + new Date().getTime() + "&callback=?", function(i) {
        if (typeof h != "undefined" && h != 0) {
            setAddressCityback(e)
        }
        if (typeof g != "undefined" && typeof g == "function") {
            g.apply(this, c)
        }
    })
}
function initProvince() {
    var c = jQuery.cookie("provinceId");
    if (c && c > 0) {
        jQuery("#currProvince").text(YHDPROVINCE.proviceObj["p_" + c]).show();
        var a = jQuery("#weibo");
        if (c == 2) {
            a.attr("href", "http://weibo.com/yihaodianbeijing")
        } else {
            if (c == 20) {
                a.attr("href", "http://weibo.com/yihaodianguangzhou")
            } else {
                a.attr("href", "http://weibo.com/yihaodian")
            }
        }
        if (!glaCookieHandler.check2ProvinceIsSame()) {
            glaCookieHandler.genGlaCookie({provinceId: c})
        }
    } else {
        var b = (typeof hideGlobalCookieCheckMsgFlag != "undefined" && hideGlobalCookieCheckMsgFlag == "1") ? 1 : 0;
        if (b) {
            return
        }
        if (jQuery("#p_1")[0]) {
            showProvinces()
        } else {
            showProvincesV2()
        }
    }
}
function closeProvinces(a) {
    if (a <= 0) {
        a = 1
    }
    var b = jQuery("#currProvince").text();
    if (b == "") {
        setAddressCity(a)
    } else {
        jQuery("#allProvinces").hide()
    }
}
function showProvinces() {
    var a = YHDPROVINCE.getCurentDomain();
    var b = a + "/header/selectProvincebox.do?timestamp=" + new Date().getTime() + "&callback=?";
    jQuery.getJSON(b, function(d) {
        if (!d.ERROR && d.value) {
            jQuery("#provinceboxDiv").html(d.value);
            jQuery("#allProvinces").jqm({overlay: 50,closeClass: "jqmClose",trigger: ".jqModal",overlayClass: "pop_win_bg",modal: true,toTop: true}).jqmShow().jqmAddClose(".popwinClose")
        }
        jQuery.getJSON(a + "/header/cartIsEmpty.do?callback=?", function(e) {
            if ("no" == e.value) {
                jQuery("#provincesPoptips").show()
            } else {
                jQuery("#provincesPoptips").hide()
            }
        });
        if (jQuery("#allProvinces")) {
            var c = jQuery("#allProvinces").find("#currentProvinceName");
            if (c) {
                YHDPROVINCE.getProvinceName(c.attr("proviceId"))
            }
        }
    })
}
YHDPROVINCE.checkProviceIsYhd = function() {
    return true
};
function showProvincesV2() {
    if (YHDPROVINCE.checkProviceIsYhd()) {
        if (jQuery.cookie("provinceId")) {
            YHDPROVINCE.headerSelectProvince();
            return
        }
    }
    var a = YHDPROVINCE.getCurentDomain();
    var b = a + "/header/selectProvinceboxV2.do?timestamp=" + new Date().getTime() + "&callback=?";
    jQuery.getJSON(b, function(c) {
        if (!c.ERROR) {
            YHDPROVINCE.processProvince(c)
        }
    })
}
YHDPROVINCE.processProvince = function(b) {
    if (YHDPROVINCE.checkProviceIsYhd()) {
        if (!jQuery.cookie("provinceId")) {
            YHDPROVINCE.chooseProvincePop(b)
        }
    } else {
        jQuery("#provinceboxDiv").html(YHDPROVINCE.ywOryhmProvinceInfo(b));
        jQuery("#allProvinces").jqm({overlay: 50,closeClass: "jqmClose",trigger: ".jqModal",overlayClass: "pop_win_bg",modal: true,toTop: true}).jqmShow().jqmAddClose(".popwinClose");
        jQuery.getJSON(currDomain + "/header/cartIsEmpty.do?callback=?", function(c) {
            if ("no" == c.value) {
                jQuery("#provincesPoptips").show()
            } else {
                jQuery("#provincesPoptips").hide()
            }
        });
        if (jQuery("#allProvinces")) {
            var a = jQuery("#allProvinces").find("#currentProvinceName");
            if (a) {
                YHDPROVINCE.getProvinceName(a.attr("proviceId"))
            }
        }
    }
};
YHDPROVINCE.getProvinceName = function(a) {
    jQuery("#currentProvinceName").html("<strong>" + jQuery("#p_" + a).text() + "站</strong> >>")
};
YHDPROVINCE.ywOryhmProvinceInfo = function(b) {
    var a = function() {
        var f = [];
        f.push("华东地区");
        f.push("华北东北");
        f.push("华南西南");
        f.push("华中西北");
        for (var e = 0; e < 4; e++) {
            var d = {};
            d.name = f[e];
            switch (e) {
                case 0:
                    d.value = [YHDPROVINCE.proviceObj.p_1, YHDPROVINCE.proviceObj.p_5, YHDPROVINCE.proviceObj.p_6, YHDPROVINCE.proviceObj.p_13, YHDPROVINCE.proviceObj.p_16];
                    d.index = ["1", "5", "6", "13", "16"];
                    break;
                case 1:
                    d.value = [YHDPROVINCE.proviceObj.p_2, YHDPROVINCE.proviceObj.p_3, YHDPROVINCE.proviceObj.p_4, YHDPROVINCE.proviceObj.p_32, YHDPROVINCE.proviceObj.p_8, YHDPROVINCE.proviceObj.p_9, YHDPROVINCE.proviceObj.p_10, YHDPROVINCE.proviceObj.p_11];
                    d.index = ["2", "3", "4", "32", "8", "9", "10", "11"];
                    break;
                case 2:
                    d.value = [YHDPROVINCE.proviceObj.p_20, YHDPROVINCE.proviceObj.p_21, YHDPROVINCE.proviceObj.p_22, YHDPROVINCE.proviceObj.p_14, YHDPROVINCE.proviceObj.p_12, YHDPROVINCE.proviceObj.p_7, YHDPROVINCE.proviceObj.p_23, YHDPROVINCE.proviceObj.p_24, YHDPROVINCE.proviceObj.p_25];
                    d.index = ["20", "21", "22", "14", "12", "7", "23", "24", "25"];
                    break;
                case 3:
                    d.value = [YHDPROVINCE.proviceObj.p_18, YHDPROVINCE.proviceObj.p_19, YHDPROVINCE.proviceObj.p_17, YHDPROVINCE.proviceObj.p_15, YHDPROVINCE.proviceObj.p_26, YHDPROVINCE.proviceObj.p_27, YHDPROVINCE.proviceObj.p_28, YHDPROVINCE.proviceObj.p_30, YHDPROVINCE.proviceObj.p_29];
                    d.index = ["18", "19", "17", "15", "26", "27", "28", "30", "29"];
                    break
            }
            YHDPROVINCE.provinceMap.put(e, d)
        }
    };
    var c = function(f) {
        var n = f.ipProvinceId != "undefined" ? f.ipProvinceId : "1";
        var l = f.ip ? f.ip : "";
        var k = f.ipProvinceIdStr ? f.ipProvinceIdStr : "";
        var h = f.provinceId;
        YHDPROVINCE.provinceMap = new YHDOBJECT.Map();
        a();
        var d = [];
        d.push('<div class="ap_area" id="allProvinces" style="display:none">');
        d.push('<div class="a_title"><a href="###" onclick="javascript:closeProvinces(' + n + ');return false;" class="fr popwinClose" >关闭</a>请选择您的收货省份</div>');
        for (var g = 0; g < 4; g++) {
            if (g == 0) {
                d.push('<dl class="first">')
            } else {
                d.push("<dl>")
            }
            var m = YHDPROVINCE.provinceMap.get(g);
            d.push("<dt>" + m.name + "</dt>");
            d.push("<dd>");
            for (var e = 0; e < m.value.length; e++) {
                d.push('<a id="p_' + m.index[e] + '" href="javascript:setAddressCity(' + m.index[e] + ')"' + (h == m.index[e] ? ' class="selected"' : "") + " >" + m.value[e] + "</a>")
            }
            d.push("</dd>");
            d.push("</dl>")
        }
        d.push('<p  id="provincesPoptips" class="poptips" style="display:none">因各省份所售商品不同，如果您切换省份，部分商品可能会从购物车中移除</p>');
        d.push(' <p class="ip_tips">');
        if (n != 0) {
            d.push('<span class="fr">请进入<a href="javascript:setAddressCity(' + n + ')  id="currentProvinceName" proviceId="' + n + '"></a></span>')
        }
        if (l) {
            d.push("您的IP地址是： " + l + "  " + k ? k : "")
        }
        d.push("</p>");
        d.push("</div>");
        return d.join("")
    };
    return c(b)
};
YHDPROVINCE.yhdCommonProvinceInfo = function(b, a) {
    a.push('<li>A<a id="p_13" href="javascript:void(0);">安徽</a></li>');
    a.push('<li>B<a id="p_2" href="javascript:void(0);">北京</a></li>');
    a.push('<li>C<a id="p_7" href="javascript:void(0);">重庆</a></li>');
    a.push('<li>G<a id="p_20" href="javascript:void(0);">广东</a><a id="p_21" href="javascript:void(0);">广西</a><a id="p_23" href="javascript:void(0);">贵州</a><a id="p_27" href="javascript:void(0);">甘肃</a></li>');
    a.push('<li>F<a id="p_14" href="javascript:void(0);">福建</a></li>');
    a.push('<li>H<a id="p_4" href="javascript:void(0);">河北</a><a id="p_11" href="javascript:void(0);">黑龙江</a><a id="p_22" href="javascript:void(0);">海南</a><a id="p_18" href="javascript:void(0);">湖北</a><a id="p_19" href="javascript:void(0);">湖南</a><a id="p_17" href="javascript:void(0);">河南</a></li>');
    a.push('<li>J<a id="p_5" href="javascript:void(0);">江苏</a><a id="p_10" href="javascript:void(0);">吉林</a><a id="p_15" href="javascript:void(0);">江西</a></li>');
    a.push('<li>L<a id="p_9" href="javascript:void(0);">辽宁</a></li>');
    a.push('<li>N<a id="p_8" href="javascript:void(0);">内蒙古</a><a id="p_30" href="javascript:void(0);">宁夏</a></li>');
    a.push('<li>Q<a id="p_28" href="javascript:void(0);">青海</a></li>');
    a.push('<li>S<a id="p_1" href="javascript:void(0);">上海</a><a id="p_16" href="javascript:void(0);">山东</a><a id="p_32" href="javascript:void(0);">山西</a><a id="p_12" href="javascript:void(0);">四川</a><a id="p_26"  href="javascript:void(0);">陕西</a></li>');
    a.push('<li>T<a id="p_3" href="javascript:void(0);">天津</a></li>');
    a.push('<li>X<a id="p_25" href="javascript:void(0);">西藏</a><a id="p_29" href="javascript:void(0);">新疆</a></li>');
    a.push('<li>Y<a id="p_24" href="javascript:void(0);">云南</a></li>');
    a.push('<li>Z<a id="p_6" href="javascript:void(0);">浙江</a></li>')
};
YHDPROVINCE.headerSelectProvince = function() {
    var b = $("#headerAllProvince"), a = $("#currProvince");
    if ($.trim(b.html()).length == 0) {
        YHDPROVINCE.yhdExistsProvinceInfo(b)
    }
    b.toggle();
    a.toggleClass("fold");
    $("#headerAllPvcClose").click(function() {
        c()
    });
    b.find("a").click(function() {
        c();
        a.text($(this).text());
        var d = $(this).attr("id").split("_")[1];
        setAddressCity(d);
        return false
    });
    function c() {
        a.removeClass("fold");
        b.hide()
    }
};
YHDPROVINCE.yhdExistsProvinceInfo = function(b) {
    var a = [];
    a.push('<li><h4><i id="headerAllPvcClose"></i>请根据您的收货地址选择</h4></li>');
    YHDPROVINCE.yhdCommonProvinceInfo(null, a);
    b.html(a.join(""))
};
YHDPROVINCE.yhdExistProvinceHoverEvent = function() {
    if (jQuery("#headerSelectProvince")[0] && currSiteId == 1) {
        var a;
        jQuery("#headerSelectProvince").hover(function() {
            a = setTimeout(function() {
                showProvincesV2();
                jQuery("#currProvince").addClass("hd_fold")
            }, 200)
        }, function() {
            if (a) {
                clearTimeout(a)
            }
            var c = jQuery("#headerAllProvince"), b = jQuery("#currProvince");
            b.removeClass("fold");
            b.removeClass("hd_fold");
            c.hide()
        })
    }
};
YHDPROVINCE.yhdNoExistsProvinceInfo = function(b) {
    var a = [];
    a.push('<div class="province_box" id="provinceBox">');
    a.push('<div class="province_title">');
    a.push("<h4>欢迎来到1号店</h4>");
    a.push("<p>目前我们提供31个地区的配送服务,请根据您的收货地址选择站点。</p>");
    a.push("</div>");
    a.push('<div class="province_select">');
    a.push('<div class="province_input">');
    a.push('<div class="province_input_con">');
    a.push('<span id="selectProvince" class="notsure">请选择</span>');
    a.push('<ul id="allProvinceSelect" class="provinceList">');
    YHDPROVINCE.yhdCommonProvinceInfo(b, a);
    a.push("</ul>");
    a.push("送货至");
    a.push("</div>");
    a.push("您的商品将会：");
    a.push("</div>");
    a.push('<p><button id="startShopping" class="disabled">开始购物<span></span></button></p>');
    a.push("</div>");
    a.push("</div>");
    yhdLib.popwin({fix: false,popcontentstr: a.join(""),fun: ["globalChangeTop"]})
};
function globalChangeTop() {
    $(".popGeneral .notsure").click(function() {
        $(".popGeneral .provinceList").show();
        var f = $(window).height(), c = $(".popGeneral .province_box").height() + $(".popGeneral .provinceList").height() - 115, d = f - c, b = $(".popGeneral").offset().top;
        if (b > d) {
            if (c > f) {
                $(".popGeneral").stop().animate({"margin-top": -f / 2}, 300)
            } else {
                var e = parseInt($(".popGeneral").css("margin-top")), a = b - d;
                $(".popGeneral").stop().animate({"margin-top": e - a}, 300)
            }
        }
    })
}
YHDPROVINCE.chooseProvincePop = function(h) {
    YHDPROVINCE.yhdNoExistsProvinceInfo(h);
    var c = h.ipProvinceId != "undefined" && h.ipProvinceId ? h.ipProvinceId : "1";
    var i = h.ipProvinceIdStr != "undefined" ? h.ipProvinceIdStr : "上海";
    var b = -1;
    var d = false, j = $("#provinceboxDiv"), g = $("#selectProvince"), a = $("#allProvinceSelect"), e = $("#startShopping");
    function f(l, k) {
        b = l;
        g.removeClass("notsure fold").html(k);
        $("#currProvince").html(k).show();
        a.hide();
        e.removeClass("disabled")
    }
    if (c && i) {
        d = true;
        f(c, i)
    }
    if (!d) {
        g.addClass("notsure");
        e.addClass("disabled")
    }
    g.click(function() {
        var k = $(this);
        if (!k.hasClass("fold")) {
            k.addClass("notsure fold");
            a.show();
            return false
        }
    });
    a.click(function() {
        return false
    });
    a.find("a").click(function() {
        d = true;
        f($(this).attr("id").split("_")[1], $(this).text())
    });
    $("#provinceBox").click(function() {
        if (g.hasClass("fold")) {
            a.hide();
            g.removeClass("fold");
            if (d) {
                g.removeClass("notsure")
            }
        }
    });
    e.click(function() {
        if ($(this).hasClass("disabled")) {
            return
        }
        j.hide();
        if (b != -1) {
            setAddressCity(b)
        }
    })
};
jQuery(document).ready(function() {
    if (isIndex != 1) {
        initProvince()
    }
    YHDPROVINCE.yhdExistProvinceHoverEvent()
});
jQuery.easing.jswing = jQuery.easing.swing;
jQuery.extend(jQuery.easing, {def: "easeOutQuad",swing: function(j, i, b, c, d) {
        return jQuery.easing[jQuery.easing.def](j, i, b, c, d)
    },easeInQuad: function(j, i, b, c, d) {
        return c * (i /= d) * i + b
    },easeOutQuad: function(j, i, b, c, d) {
        return -c * (i /= d) * (i - 2) + b
    },easeInOutQuad: function(j, i, b, c, d) {
        if ((i /= d / 2) < 1) {
            return c / 2 * i * i + b
        }
        return -c / 2 * ((--i) * (i - 2) - 1) + b
    },easeInCubic: function(j, i, b, c, d) {
        return c * (i /= d) * i * i + b
    },easeOutCubic: function(j, i, b, c, d) {
        return c * ((i = i / d - 1) * i * i + 1) + b
    },easeInOutCubic: function(j, i, b, c, d) {
        if ((i /= d / 2) < 1) {
            return c / 2 * i * i * i + b
        }
        return c / 2 * ((i -= 2) * i * i + 2) + b
    },easeInQuart: function(j, i, b, c, d) {
        return c * (i /= d) * i * i * i + b
    },easeOutQuart: function(j, i, b, c, d) {
        return -c * ((i = i / d - 1) * i * i * i - 1) + b
    },easeInOutQuart: function(j, i, b, c, d) {
        if ((i /= d / 2) < 1) {
            return c / 2 * i * i * i * i + b
        }
        return -c / 2 * ((i -= 2) * i * i * i - 2) + b
    },easeInQuint: function(j, i, b, c, d) {
        return c * (i /= d) * i * i * i * i + b
    },easeOutQuint: function(j, i, b, c, d) {
        return c * ((i = i / d - 1) * i * i * i * i + 1) + b
    },easeInOutQuint: function(j, i, b, c, d) {
        if ((i /= d / 2) < 1) {
            return c / 2 * i * i * i * i * i + b
        }
        return c / 2 * ((i -= 2) * i * i * i * i + 2) + b
    },easeInSine: function(j, i, b, c, d) {
        return -c * Math.cos(i / d * (Math.PI / 2)) + c + b
    },easeOutSine: function(j, i, b, c, d) {
        return c * Math.sin(i / d * (Math.PI / 2)) + b
    },easeInOutSine: function(j, i, b, c, d) {
        return -c / 2 * (Math.cos(Math.PI * i / d) - 1) + b
    },easeInExpo: function(j, i, b, c, d) {
        return (i == 0) ? b : c * Math.pow(2, 10 * (i / d - 1)) + b
    },easeOutExpo: function(j, i, b, c, d) {
        return (i == d) ? b + c : c * (-Math.pow(2, -10 * i / d) + 1) + b
    },easeInOutExpo: function(j, i, b, c, d) {
        if (i == 0) {
            return b
        }
        if (i == d) {
            return b + c
        }
        if ((i /= d / 2) < 1) {
            return c / 2 * Math.pow(2, 10 * (i - 1)) + b
        }
        return c / 2 * (-Math.pow(2, -10 * --i) + 2) + b
    },easeInCirc: function(j, i, b, c, d) {
        return -c * (Math.sqrt(1 - (i /= d) * i) - 1) + b
    },easeOutCirc: function(j, i, b, c, d) {
        return c * Math.sqrt(1 - (i = i / d - 1) * i) + b
    },easeInOutCirc: function(j, i, b, c, d) {
        if ((i /= d / 2) < 1) {
            return -c / 2 * (Math.sqrt(1 - i * i) - 1) + b
        }
        return c / 2 * (Math.sqrt(1 - (i -= 2) * i) + 1) + b
    },easeInElastic: function(o, m, p, a, b) {
        var d = 1.70158;
        var c = 0;
        var n = a;
        if (m == 0) {
            return p
        }
        if ((m /= b) == 1) {
            return p + a
        }
        if (!c) {
            c = b * 0.3
        }
        if (n < Math.abs(a)) {
            n = a;
            var d = c / 4
        } else {
            var d = c / (2 * Math.PI) * Math.asin(a / n)
        }
        return -(n * Math.pow(2, 10 * (m -= 1)) * Math.sin((m * b - d) * (2 * Math.PI) / c)) + p
    },easeOutElastic: function(o, m, p, a, b) {
        var d = 1.70158;
        var c = 0;
        var n = a;
        if (m == 0) {
            return p
        }
        if ((m /= b) == 1) {
            return p + a
        }
        if (!c) {
            c = b * 0.3
        }
        if (n < Math.abs(a)) {
            n = a;
            var d = c / 4
        } else {
            var d = c / (2 * Math.PI) * Math.asin(a / n)
        }
        return n * Math.pow(2, -10 * m) * Math.sin((m * b - d) * (2 * Math.PI) / c) + a + p
    },easeInOutElastic: function(o, m, p, a, b) {
        var d = 1.70158;
        var c = 0;
        var n = a;
        if (m == 0) {
            return p
        }
        if ((m /= b / 2) == 2) {
            return p + a
        }
        if (!c) {
            c = b * (0.3 * 1.5)
        }
        if (n < Math.abs(a)) {
            n = a;
            var d = c / 4
        } else {
            var d = c / (2 * Math.PI) * Math.asin(a / n)
        }
        if (m < 1) {
            return -0.5 * (n * Math.pow(2, 10 * (m -= 1)) * Math.sin((m * b - d) * (2 * Math.PI) / c)) + p
        }
        return n * Math.pow(2, -10 * (m -= 1)) * Math.sin((m * b - d) * (2 * Math.PI) / c) * 0.5 + a + p
    },easeInBack: function(l, k, b, c, d, j) {
        if (j == undefined) {
            j = 1.70158
        }
        return c * (k /= d) * k * ((j + 1) * k - j) + b
    },easeOutBack: function(l, k, b, c, d, j) {
        if (j == undefined) {
            j = 1.70158
        }
        return c * ((k = k / d - 1) * k * ((j + 1) * k + j) + 1) + b
    },easeInOutBack: function(l, k, b, c, d, j) {
        if (j == undefined) {
            j = 1.70158
        }
        if ((k /= d / 2) < 1) {
            return c / 2 * (k * k * (((j *= (1.525)) + 1) * k - j)) + b
        }
        return c / 2 * ((k -= 2) * k * (((j *= (1.525)) + 1) * k + j) + 2) + b
    },easeInBounce: function(j, i, b, c, d) {
        return c - jQuery.easing.easeOutBounce(j, d - i, 0, c, d) + b
    },easeOutBounce: function(j, i, b, c, d) {
        if ((i /= d) < (1 / 2.75)) {
            return c * (7.5625 * i * i) + b
        } else {
            if (i < (2 / 2.75)) {
                return c * (7.5625 * (i -= (1.5 / 2.75)) * i + 0.75) + b
            } else {
                if (i < (2.5 / 2.75)) {
                    return c * (7.5625 * (i -= (2.25 / 2.75)) * i + 0.9375) + b
                } else {
                    return c * (7.5625 * (i -= (2.625 / 2.75)) * i + 0.984375) + b
                }
            }
        }
    },easeInOutBounce: function(j, i, b, c, d) {
        if (i < d / 2) {
            return jQuery.easing.easeInBounce(j, i * 2, 0, c, d) * 0.5 + b
        }
        return jQuery.easing.easeOutBounce(j, i * 2 - d, 0, c, d) * 0.5 + c * 0.5 + b
    }});
(function() {
    loli = window.loli || {};
    loli.ui = loli.ui || {};
    loli.ui.switchable = function(h, e, g) {
        function f(O, K, W) {
            var c = this, U = {triggerType: "mouseenter",effect: "none",easing: "easeOutQuart",duration: 500,delay: 100,interval: 5000,visible: 1,steps: 1,autoPlay: false,circular: false,prefix: "L_tab"};
            var K = c.config = $.extend(U, K);
            if (K.autoPlay) {
                K.circular = true
            }
            if (K.triggerType == "click") {
                K.delay = 0
            }
            c.activeIndex = 0;
            var M = K.effect, Q = K.steps, X = K.visible, aa = K.duration, L = K.panelClass, H = K.circular, G = K.easing, b = K.prefix, J = b + "_prev", ab = b + "_prev_disabled", I = b + "_next", T = b + "_next_disabled";
            var R = O.find("." + b + "_panel"), F = R.first(), N = O.find("." + b + "_trigger"), Y = O.find("." + b + "_arrow");
            var Z = R.length, a = Z / X;
            var V;
            N.bind(K.triggerType, function() {
                var i = $(this).index(), k = c.activeIndex;
                if (i == k) {
                    return
                }
                var j = (i > k) ? "forward" : "backward";
                V = setTimeout(function() {
                    c.switchTo(i, j)
                }, K.delay)
            });
            if (K.triggerType == "mouseenter") {
                N.bind("mouseleave", function() {
                    clearTimeout(V)
                })
            }
            Y.bind("click", function() {
                if ((M == "scrollX" || M == "scrollY") && R.parent().is(":animated")) {
                    return
                }
                var i = $(this);
                if (i.hasClass(ab) || i.hasClass(T)) {
                    return
                }
                if (i.hasClass(J)) {
                    var j = c.activeIndex - 1;
                    c.switchTo(c.activeIndex - 1, "backward")
                } else {
                    c.switchTo(c.activeIndex + 1, "forward")
                }
            });
            var P = function(i) {
                var l = c.activeIndex, j, k;
                if (l > -1) {
                    j = R.slice(l * X, (l + 1) * X)
                } else {
                    j = null
                }
                k = R.slice(i * X, (i + 1) * X);
                return {fromPanels: j,toPanels: k}
            };
            c.switchTo = function(i, j) {
                var k = c.activeIndex;
                if (j == "backward" && k === 0) {
                    i = i + a
                } else {
                    if (j == "forward" && k == a - 1) {
                        i = i - a
                    }
                }
                N.eq(k).removeClass("cur");
                N.eq(i).addClass("cur");
                var l = d[M];
                l(i, j);
                c.activeIndex = i;
                if (!H) {
                    if (c.activeIndex === 0) {
                        O.find("." + J).addClass(ab)
                    } else {
                        if (c.activeIndex == (a - 1)) {
                            O.find("." + I).addClass(T)
                        } else {
                            Y.removeClass(ab + " " + T)
                        }
                    }
                }
                c.toPanels = P(i).toPanels;
                W && W.call(c)
            };
            var d = {none: function(i) {
                    var j = P(i);
                    j.fromPanels.hide();
                    j.toPanels.show()
                },fade: function(i) {
                    var j = P(i);
                    j.fromPanels.animate({opacity: 0}, aa);
                    j.toPanels.animate({opacity: 1}, aa)
                },scroll: function(o, l) {
                    var j = l === "forward", i = K.effect == "scrollX", m = R.parent();
                    m.stop();
                    if (i) {
                        var s = "left", r = "marginLeft", q = F.outerWidth(true), u = q * Q
                    } else {
                        var s = "top", r = "marginTop", q = F.outerHeight(true), u = q * Q
                    }
                    var t = {};
                    if ((j && o === 0) || (!j && o === a - 1)) {
                        var k = {position: "relative"}, p = P(o).toPanels;
                        k[s] = (j ? 1 : -1) * u * a;
                        p.css(k);
                        t[r] = j ? -u * a : u;
                        m.animate(t, aa, G, function() {
                            $(this).css(r, j ? 0 : -u * (a - 1));
                            n(p, s)
                        })
                    } else {
                        t[r] = -o * u;
                        m.animate(t, aa, G, function() {
                            if (R.eq(0).css("position") == "relative") {
                                n(R.slice(0, X), s)
                            } else {
                                if (R.eq(Z - 1).css("position") == "relative") {
                                    n(R.slice((a - 1) * X, a * X), s)
                                }
                            }
                        })
                    }
                    function n(x, v) {
                        var w = {position: "static"};
                        w[v] = 0;
                        x.css(w)
                    }
                }};
            d.scrollX = d.scrollY = d.scroll;
            var S = function() {
                if (!H) {
                    O.find("." + J).addClass(ab);
                    if (Z <= X) {
                        O.find("." + I).addClass(T)
                    }
                }
                switch (M) {
                    case "none":
                        R.slice(0, X).show();
                        break;
                    case "fade":
                        R.slice(0, X).css("opacity", 1);
                        break;
                    default:
                        break
                }
                O.data("L_inited", true)
            };
            if (!O.data("L_inited")) {
                S()
            }
            if (K.autoPlay) {
                c.start();
                O.hover(function() {
                    c.stop()
                }, function() {
                    c.start()
                })
            }
            return this
        }
        f.prototype.start = function() {
            var a = this;
            if (a.autoPlay) {
                return
            }
            a.autoPlay = setInterval(function() {
                a.switchTo(a.activeIndex + 1, "forward")
            }, a.config.interval)
        };
        f.prototype.stop = function() {
            clearInterval(this.autoPlay);
            this.autoPlay = undefined
        };
        return new f(h, e, g)
    }
})();
(function(c) {
    var a = window.loli || (window.loli = {});
    var b = null;
    var e = 0;
    var d = new Date().getTime();
    var g = 10 * 60 * 1000;
    var f = [];
    a.globalCheckLogin = function(i) {
        j(i);
        function j(m) {
            if (!jQuery.cookie("ut") && !jQuery.cookie("aut")) {
                m({result: "0",userName: ""});
                return
            }
            var l = (new Date()).getTime();
            if (l - d > g) {
                e = 0
            }
            if (e == 0) {
                k(m);
                d = new Date().getTime();
                return
            } else {
                if (e == 2) {
                    if (m && b) {
                        m(b)
                    }
                } else {
                    if (m) {
                        f.push(m)
                    }
                }
            }
        }
        function k(m) {
            e = 1;
            var l = URLPrefix.passport + "/publicPassport/isLogin.do?callback=?";
            jQuery.getJSON(l, function(n) {
                h(m, n)
            })
        }
        function h(o, m) {
            e = 2;
            d = (new Date()).getTime();
            if (m) {
                b = m;
                if (o) {
                    o(m)
                }
                var l = f.length;
                for (var n = 0; n < l; n++) {
                    var o = f.shift();
                    o(m)
                }
            }
        }
    }
})(jQuery);
var YHDMINICART = YHDMINICART || {};
function addToCart(c, b, e, g, f, d) {
    var a = {};
    a.amount = g;
    a.isFloat = f;
    a.linkPosition = d;
    a.merchantId = e;
    addToCartNew(c, b, a)
}
function addToCartNew(b, e, c) {
    var g = c.amount;
    var d = c.isFloat;
    var h = c.linkPosition;
    var i = c.merchantId;
    var f = c.ybPmIds;
    var a = currDomain + "/cart/phone/isContractProduct.do?productId=" + e + "&merchantId=" + i;
    if (f) {
        a = a + "&ybPmIds=" + f
    }
    a = a + "&callback=?";
    jQuery.getJSON(a, function(j) {
        if (j.ERROR) {
        } else {
            if (j) {
                var k = parseInt(j.code);
                if (k == 1) {
                    addIphone4ToCart(b, e, i, g, d)
                } else {
                    if (jQuery("#validateProductId").length > 0) {
                        jQuery("#validateProductId").attr("value", e)
                    }
                    if (jQuery.cookie("prompt_flag") == null && jQuery("#buyPromptDiv").length > 0) {
                        YHD.popwinId("buyPromptDiv", "popwinClose");
                        jQuery("#validate").bind("click", function() {
                            doAddToCart(b, e, c)
                        })
                    } else {
                        doAddToCart(b, e, c)
                    }
                }
            }
        }
    })
}
function addIphone4ToCart(c, b, d, a, e) {
    if (jQuery("#validateProductId").length > 0) {
        jQuery("#validateProductId").attr("value", b)
    }
    if (jQuery.cookie("prompt_flag") == null && jQuery("#buyPromptDiv").length > 0) {
        YHD.popwinId("buyPromptDiv");
        jQuery("#validate").bind("click", function() {
            doAddIphone4ToCart(c, b, d, a)
        })
    } else {
        doAddIphone4ToCart(c, b, d, a)
    }
}
function doAddIphone4ToCart(a, d, b, c) {
    window.location.href = URLPrefix.productDetailHost + "/product/" + d + "_" + b
}
function doAddToCart(a, d, c) {
    if (isPrescriotionForCheckAddToCart(d)) {
        var b = parseInt(jQuery("#buyButton_" + d).attr("specialType"));
        YHD.alertPrescriotion(b, function() {
            processDoAddToCart(a, d, c)
        })
    } else {
        processDoAddToCart(a, d, c)
    }
}
function isPrescriotionForCheckAddToCart(b) {
    var a = false;
    if (jQuery("#buyButton_" + b).size() > 0) {
        var c = jQuery("#buyButton_" + b).attr("specialType");
        if (c != null && (parseInt(c) >= 14 && parseInt(c) <= 18)) {
            a = true
        }
    }
    return a
}
function processDoAddToCart(h, k, i) {
    var a = i.amount;
    var j = i.isFloat;
    var b = i.linkPosition;
    var f = i.merchantId;
    var c = i.pmId;
    var l = i.ybPmIds;
    var e = encodeURIComponent(document.referrer);
    var d = b ? b : "";
    var g = currDomain + "/cart/opt/add.do?productId=" + k + "&merchantId=" + f;
    if (c) {
        g += "&pmId=" + c
    }
    if (l) {
        g += "&ybPmIds=" + l
    }
    g += "&num=" + a + "&pageRef=" + e + "&linkPosition=" + d + "&callback=?";
    jQuery.getJSON(g, function(n) {
        if (n) {
            var m = n.code;
            if (m == "300010801005") {
                window.location.href = currDomain + n.data
            } else {
                if (m == "300010800001") {
                    var p = URLPrefix.passport;
                    yhdPublicLogin.showLoginDivNone(p, false, "", function(q) {
                        if (q == 0) {
                            yhdPublicLogin.showTopLoginInfo()
                        }
                    })
                } else {
                    if (m == "00000000") {
                        if (j) {
                            var o = function() {
                                if (floatCartShowTime) {
                                    clearTimeout(floatCartShowTime);
                                    floatCartShowTime = 0
                                }
                            };
                            o();
                            jQuery("#showMiniCart").show();
                            floatCartShowTime = setTimeout(function() {
                                jQuery("#showMiniCart").hide(1000);
                                o()
                            }, 2000);
                            jQuery("#showMiniCart").mouseenter(o)
                        } else {
                            if (!i.isDeleteNewDiv) {
                                afterAddToCartPopwin(n, k, i)
                            }
                        }
                        reloadMiniCart();
                        YHDOBJECT.callBackFunc(i)
                    } else {
                        afterAddToCartPopwin(n, k, i)
                    }
                }
            }
        }
    })
}
function afterAddToCartPopwin(c, b, a) {
    var h, e = [], g = jQuery.cookie("provinceId"), k;
    h = "商品加入购物车";
    e.push('<div id="addCartWin" class="spop">');
    e.push('<div class="spopro clearfix">');
    if (c.code == "00000000") {
        var i = c.data, j = i.pic115x115 || (URLPrefix.image + "/images/defaultproduct_115x115.jpg"), d = i.num || 0, f = i.amount || 0;
        e.push('<div class="spopimg"><img src="' + j + '" width="115" height="115" /></div><div class="spopbox">');
        e.push('<strong class="spopstitle">' + i.name + "</strong>");
        e.push("<span>加入数量：<b>" + d + "</b></span>");
        e.push("<span>总计金额：<b>&yen;" + f + "</b></span>");
        e.push('<div class="spopbtn"><a href="javascript:void(0);" class="sview close_tcg" onclick="addTrackPositionToCookie(\'1\',\'product_popup_jxgw\');" >继续购物</a><a href="javascript:void(0);" class="sbuy" onclick="addTrackPositionToCookie(\'1\',\'product_popup\');window.location=\' ' + currDomain + "/cart/cart.do?action=view';return false;\" >去结算</a></div></div>")
    } else {
        h += "失败";
        e.push('<div class="failed_msg">' + c.msg + "</div>")
    }
    e.push("</div>");
    k = URLPrefix.pms + "/pms/getRecommProducts.do?currSiteId=" + currSiteId + "&provinceId=" + g + "&productid=" + b + "&merchantId=" + a.merchantId + "&type=html&callback=?";
    jQuery.getJSON(k, function(l) {
        if (l && l.value) {
            e.push(l.value)
        }
        e.push("</div>");
        e = e.join("");
        yhdLib.popwin({poptitle: h,popcontentstr: e})
    })
}
var floatCartShowTime = 0;
function buildCartNumber() {
    var a = getCartProductNum();
    jQuery("#in_cart_num").html(a)
}
function getCartProductNum() {
    var b = jQuery.cookie("cart_cookie_uuid");
    var a = 0;
    if (b) {
        a = parseInt(jQuery.cookie("cart_num")) > 999 ? "999+" : parseInt(jQuery.cookie("cart_num"))
    }
    return a ? a : 0
}
function loadMiniCart() {
    if (!jQuery("#in_cart_num").data("isLoaded")) {
        jQuery("#in_cart_num").data("isLoaded", true);
        reloadMiniCart()
    }
}
function reloadMiniCart(c) {
    var b = this;
    var a = currDomain + "/cart/info/cart.do?callback=?";
    jQuery.getJSON(a, function(d) {
        if (d && d.code == "00000000") {
            jQuery("#showMiniCart").css("height", "auto");
            afterLoadMiniCart(d.data);
            if (c && (typeof c == "function")) {
                c.apply(b, [d.data])
            }
        } else {
            afterLoadMiniCart()
        }
    })
}
function yhdSiteLoadMiniCart(l) {
    var d = jQuery("#showMiniCartDetail");
    var k = "";
    var e;
    var c = "YHD_TOP_minicart";
    if (l && l.totalNum && l.items) {
        var g = parseInt(l.totalNeedPoint);
        var a = parseInt(l.totalNeedZhongxinPoint);
        var j = parseFloat(l.totalNeedMoney);
        var h = parseFloat(l.totalAmount) + j;
        h = h.toFixed(2);
        var b = l.currProvinceId;
        k = "<ul>";
        jQuery(l.items).each(function(A) {
            var r = this;
            var B = parseInt(r.itemType);
            var u = parseInt(r.warningType);
            var s = parseInt(r.pointBuyType);
            var w = r.hasPromoteLimitAttachedKey;
            var y = parseInt(r.promotionContentType);
            var z = r.promotionLevelId;
            var x = false;
            if (B == 0 && (y == 3 || y == 9 || y == 10)) {
                x = true
            }
            if (u > 0) {
                k += '<li id="mini_cart_li_' + A + '" class="miniSoldout">'
            } else {
                k += '<li id="mini_cart_li_' + A + '">'
            }
            if (x) {
                var q = URLPrefix.search + "/p/pt" + r.promotionId + "-pl" + z;
                k += '<a traget="_blank" class="pro_img" href="' + q + '" data-ref="' + c + '"><img alt="' + r.cnName + '" src="' + URLPrefix.statics + '/global/images/promotion_mix.jpg"></a>';
                k += '<a traget="_blank" class="pro_name"  href="' + q + '" data-ref="' + c + '">' + r.cnName + "</a>"
            } else {
                k += '<a traget="_blank" class="pro_img" href="' + URLPrefix.productDetailHost + "/item/" + r.pmInfoId + '" data-ref="' + c + '"><img alt="' + r.cnName + '" src="' + r.picture4040URL + '"></a>';
                k += '<a traget="_blank" class="pro_name"  href="' + URLPrefix.productDetailHost + "/item/" + r.pmInfoId + '" data-ref="' + c + '">' + r.cnName + "</a>"
            }
            if (u <= 0) {
                k += '<span class="pro_price">';
                var m = parseInt(r.num);
                var t = r.totalPrice;
                t = t.toFixed(2);
                if (x) {
                    k += "&yen;" + r.totalPrice
                } else {
                    if (s && s > 0) {
                        var o = parseFloat(r.needPoint);
                        o = o.toFixed(0);
                        if (s == 1) {
                            k += o + '积分<p class="cart_gray">(' + chineseUrl + ")</p>"
                        } else {
                            if (s == 2) {
                                var n = r.needMoney;
                                k += "(" + o + "积分)<p>&yen;" + n + "</p>"
                            } else {
                                if (s == 3) {
                                    k += o + '积分<p class="cart_gray">(中信)</p>'
                                } else {
                                    if (s == 4) {
                                        k += '0<p class="cart_gray">(积分抽奖)</p>'
                                    } else {
                                        k += "&yen;" + t
                                    }
                                }
                            }
                        }
                    } else {
                        if (r.activityId != null && r.activityId != "0") {
                            k += "&yen;" + r.totalPrice
                        } else {
                            if (B == 3 && r.needPoint && r.needPoint > 0) {
                                var o = r.needPoint;
                                var n = r.totalPrice;
                                k += "(" + o + "积分)<p>&yen;" + n + "</p>"
                            } else {
                                k += "&yen;" + t
                            }
                        }
                    }
                }
            } else {
                if (u == 2) {
                    k += "<span>抢购受限"
                } else {
                    k += "<span>已售完"
                }
            }
            k += "</span>";
            k += '<div class="num_box">';
            if (currSiteId == 1) {
                if (u <= 0) {
                    k += yhdMiniCart.loadModifyNumInfo(r)
                }
            }
            if (!x) {
                if (s != 4 && (r.activityId == null || r.activityId == "0" || r.activityId == "-55")) {
                    var B = r.itemType, y = r.promotionContentType, v, p;
                    if (B == 0 && y > 0 && y != 3 && y != 9 && y != 10) {
                        v = "normal_" + r.promotionId + "_" + r.promotionLevelId + "_" + r.merchantId + "_" + r.productId + "_" + r.num, p = "deletePromo"
                    } else {
                        v = r.identifier;
                        p = "deleteId"
                    }
                    k += '<a href="javascript:void(0);" onclick="ajaxDeleteMiniCartItem(\'' + p + "','" + v + "');";
                    k += "return false;gotracker('2','YHD_TOP_delShop_" + A + "','" + r.productId + "')\">删除</a>"
                }
            }
            k += "</div>";
            k += "</li>"
        });
        k += "</ul>";
        var i = "合计";
        k += '<div class="checkout_box">';
        k += '<p><span class="fl">共<strong>' + l.totalNum + "</strong>件商品</span>" + i + "：<strong>&yen;" + h + "</strong></p>";
        k += '<a href="' + currDomain + '/cart/cart.do?action=view" class="checkout_btn" data-ref="' + c + '">去结算</a>';
        k += "</div>";
        jQuery("#in_cart_num").text(parseInt(l.totalNum) > 999 ? "999+" : l.totalNum)
    } else {
        k = '<div class="none_tips">';
        jQuery("#in_cart_num").text("0");
        k += "您的购物车里还没有" + chineseUrl + "的商品，欢迎选购！";
        k += "</div>"
    }
    f(d, k);
    function f(m, n) {
        m.html(n);
        yhdMiniCart.bindMiniCartEvent();
        m.data("inani", false);
        if (typeof e != "undefined" && e) {
            clearTimeout(e)
        }
    }
}
function afterLoadMiniCart(a) {
    yhdSiteLoadMiniCart(a)
}
function ajaxDeleteMiniCartItem(b, a) {
    var c = currDomain + "/cart/opt/delete.do?" + b + "=" + a + "&callback=?";
    var d = {rd: Math.random()};
    jQuery.getJSON(c, d, function(e) {
        reloadMiniCart()
    })
}
var yhdMiniCart = yhdMiniCart || {};
if (typeof currSiteId != "undefined" && currSiteId == 1) {
    yhdMiniCart.Map = function() {
        var a = 0;
        this.entry = {};
        this.put = function(b, c) {
            if (!this.containsKey(b)) {
                a++
            }
            this.entry[b] = c
        };
        this.get = function(b) {
            if (this.containsKey(b)) {
                return this.entry[b]
            } else {
                return null
            }
        };
        this.remove = function(b) {
            if (delete this.entry[b]) {
                a--
            }
        };
        this.containsKey = function(b) {
            return (b in this.entry)
        };
        this.containsValue = function(b) {
            for (var c in this.entry) {
                if (this.entry[c] == b) {
                    return true
                }
            }
            return false
        };
        this.values = function() {
            var b = [];
            for (var c in this.entry) {
                b.push(this.entry[c])
            }
            return b
        };
        this.keys = function() {
            var b = new Array(a);
            for (var c in this.entry) {
                b.push(c)
            }
            return b
        };
        this.size = function() {
            return a
        };
        this.clear = function() {
            this.entry = {};
            this.size = 0
        }
    };
    yhdMiniCart.urlMap = new yhdMiniCart.Map();
    yhdMiniCart.ajaxQueue = new Array();
    yhdMiniCart.loadModifyNumInfo = function(l) {
        var j = parseInt(l.landingNumLimit);
        var d = parseInt(l.currentStockNum);
        var o = parseInt(l.userLimitNum);
        var g = parseInt(l.totalLimitNum);
        var k = parseInt(l.promoteType);
        var i = parseInt(l.shoppingCountNum);
        var f = parseInt(l.promotionId);
        var b = 0;
        var a = parseInt(l.itemType);
        var n = parseInt(l.pointBuyType);
        var e = l.identifier;
        if (a == 3) {
            b = 2
        } else {
            if (l.hasPromoteLimitAttachedKey) {
                b = 1
            } else {
                b = 0
            }
        }
        var m = d + "|" + o + "|" + g + "|" + i + "|" + j + "|" + k;
        var h = l.productId + "|" + l.merchantId + "|" + b + "|" + f;
        var c = "";
        if (n > 0 || (a == 4 || a == 0) || (a == 3 && j == 1)) {
            c += '<b class="minusDisable"></b>';
            c += '<input type="text" id = ' + e + " oriNum=" + l.num + ' class="minicart_num"  value=' + l.num + ' disabled="disabled" class="disable" />';
            c += '<b class="plusDisable"></b>'
        } else {
            if (l.num == 1) {
                c += '<b class="minusDisable"></b>'
            } else {
                c += '<b class="minus" ></b>'
            }
            c += '<input type="text" id = ' + e + " oriNum=" + l.num + ' class="minicart_num" limitNum=' + m + "  value=" + l.num + " paramValue= " + h + " />";
            c += '<b class="plus" ></b>'
        }
        return c
    };
    yhdMiniCart.clickPlusCalSubTotal = function() {
        loli.delay(".plus", "click", function() {
            var a = this.siblings("input");
            return yhdMiniCart.ajaxNum(a, "increment")
        }, function() {
            yhdMiniCart.ajaxPost()
        }, 1000)
    };
    yhdMiniCart.clickMinusCalSubTotal = function() {
        loli.delay(".minus", "click", function() {
            var b = this.siblings("input");
            if (parseInt(b.val()) == 1) {
                var a = "输入的数量有误,应为[1-999]";
                yhdMiniCart.showWarningMsg(b, a);
                return false
            }
            return yhdMiniCart.ajaxNum(b, "decrement")
        }, function() {
            yhdMiniCart.ajaxPost()
        }, 1000)
    };
    yhdMiniCart.handCalSubTotal = function() {
        loli.delay("#showMiniCartDetail ul li div input[type=text]", "keyup", function() {
        }, function() {
            yhdMiniCart.ajaxNum(this);
            yhdMiniCart.ajaxPost()
        }, 2000)
    };
    yhdMiniCart.ajaxNum = function(a, b) {
        if (b == "increment") {
            a.val(parseInt(a.val()) + 1)
        } else {
            if (b == "decrement") {
                a.val(parseInt(a.val()) - 1)
            }
        }
        var c = /^[1-9]\d{0,2}$/g;
        if (!a.val().match(c)) {
            var g = "输入的数量有误,应为[1-999]";
            yhdMiniCart.showWarningMsg(a, g);
            a.val(a.attr("oriNum"));
            return false
        }
        if (parseInt(a.val()) == parseInt(a.attr("oriNum"))) {
            return
        }
        var f = a.attr("paramValue");
        var d = f.split("|");
        var e = d[2];
        if (e == 2) {
            return yhdMiniCart.calLandingTotal(a)
        } else {
            return yhdMiniCart.calSubTotal(a)
        }
    };
    yhdMiniCart.ajaxPost = function() {
        yhdMiniCart.ajaxQueue = yhdMiniCart.urlMap.values();
        yhdMiniCart.urlMap.clear();
        yhdMiniCart.sendAjaxReq(yhdMiniCart.ajaxQueue)
    };
    yhdMiniCart.sendAjaxReq = function(d) {
        if (d.length == 0) {
            reloadMiniCart();
            return
        }
        var a = d.shift();
        var b = a.url;
        var c = a.obj;
        jQuery.getJSON(b + "&callback=?", function(g) {
            var f = g.code;
            if (f == "00000000") {
                yhdMiniCart.sendAjaxReq(d)
            } else {
                if ((f.substr(0, 3) + f.substr(7, 5)) == "30000001") {
                    var e = URLPrefix.passport;
                    yhdPublicLogin.showLoginDivNone(e, false, "", function(h) {
                        if (h == 0) {
                            yhdPublicLogin.showTopLoginInfo()
                        }
                    })
                } else {
                    yhdMiniCart.showWarningMsg(c, g.msg);
                    yhdMiniCart.sendAjaxReq(d)
                }
            }
        })
    };
    yhdMiniCart.calSubTotal = function(d) {
        var f = d.val();
        var a = /^[1-9]\d{0,2}$/g;
        if (!f.match(a)) {
            var m = "输入的数量有误,应为[1-999]";
            yhdMiniCart.showWarningMsg(d, m);
            d.val(d.attr("oriNum"));
            return false
        }
        if (parseInt(f) > 1) {
            yhdMiniCart.setMinusOperate(d, true)
        } else {
            if (parseInt(f) == 1) {
                yhdMiniCart.setMinusOperate(d, false)
            }
        }
        var l = d.attr("paramValue");
        var n = l.split("|");
        var e = parseInt(isNaN(n[0]) ? 0 : n[0]);
        var h = parseInt(isNaN(n[1]) ? 0 : n[1]);
        var c = parseInt(isNaN(n[2]) ? 0 : n[2]);
        var j = parseInt(isNaN(n[3]) ? 0 : n[3]);
        var g = d.attr("id");
        if (!yhdMiniCart.checkOverLimit(d)) {
            f = parseInt(d.val());
            yhdMiniCart.showMinusTips(d, f, c, e, h, j);
            var b = yhdMiniCart.calItemNum(e, h, c, f, j);
            b = b > 999 ? 999 : b;
            var k = currDomain + "/cart/opt/editNum.do?cartItemVoId=" + g + "&num=" + b;
            var i = {};
            i.url = k;
            i.obj = d;
            yhdMiniCart.urlMap.put(g, i);
            return true
        }
        return false
    };
    yhdMiniCart.calLandingTotal = function(j) {
        var l = j.val();
        var i = /^[1-9]\d{0,2}$/g;
        if (!l.match(i)) {
            var f = "输入的数量有误,应为[1-999]";
            yhdMiniCart.showWarningMsg(j, f);
            j.val(j.attr("oriNum"));
            return false
        }
        if (parseInt(l) > 1) {
            yhdMiniCart.setMinusOperate(j, true)
        } else {
            if (parseInt(l) == 1) {
                yhdMiniCart.setMinusOperate(j, false)
            }
        }
        var e = j.attr("paramValue");
        var h = e.split("|");
        var k = h[0];
        var c = h[1];
        var d = h[3];
        var a = j.attr("id");
        if (!yhdMiniCart.checkOverLimit(j)) {
            l = j.val();
            var g = currDomain + "/cart/opt/editLandingNum.do?cartItemVoId=" + a + "&num=" + l + "&promotionId=" + d;
            var b = {};
            b.url = g;
            b.obj = j;
            yhdMiniCart.urlMap.put(a, b)
        }
    };
    yhdMiniCart.checkOverLimit = function(n) {
        var c = parseInt(isNaN(n.val()) ? 0 : n.val());
        var j = n.attr("oriNum");
        var o = n.attr("paramValue");
        var l = o.split("|");
        var b = l[2];
        var k = n.attr("limitNum");
        var m = k.split("|");
        var h = parseInt(m[0]);
        var a = parseInt(m[1]);
        var e = parseInt(m[2]);
        var f = parseInt(m[3]);
        var d = parseInt(m[4]);
        var i = parseInt(m[5]);
        var g = "";
        var q = c;
        var p = false;
        switch (parseInt(b)) {
            case 0:
                if (h > 0) {
                    if (c > h) {
                        g = "该商品库存有限，您最多只能购买" + h + "件";
                        q = h;
                        p = q == j
                    } else {
                        if (f > 1 && c < f) {
                            g = "该商品[" + f + "件起购]";
                            q = f;
                            p = q == j
                        } else {
                            if (i == 3) {
                                if (c > a && a > 0) {
                                    g = "该商品[每人限购" + a + "件],超过则以" + chineseUrl + "价购买"
                                }
                            } else {
                                if (i == 4) {
                                    if (a > 0 && a < e) {
                                        if (c > a) {
                                            g = "该商品[每人限购" + a + "件],超过则以" + chineseUrl + "价购买"
                                        }
                                    } else {
                                        if (e > 0 && c > e) {
                                            g = "该商品[限购" + e + "件],超过则以" + chineseUrl + "价购买"
                                        }
                                    }
                                } else {
                                    if (e > 0 && c > e && (i == 5)) {
                                        g = "该商品[限购" + e + "件]";
                                        q = e;
                                        p = q == j
                                    } else {
                                        g = ""
                                    }
                                }
                            }
                        }
                    }
                } else {
                    q = 0;
                    g = "该商品已下架或无库存"
                }
                break;
            case 1:
                if (h > 0) {
                    if (c > h) {
                        g = "该商品库存有限，您最多只能购买" + h + "件";
                        q = h;
                        p = q == j
                    }
                } else {
                    q = 0;
                    g = "该商品已下架或无库存"
                }
                break;
            case 2:
                if (d > 0 && c > d) {
                    g = "该商品为特价商品，您最多只能购买" + d + "件";
                    q = d;
                    p = q == j
                } else {
                    if (f > 1 && c < f) {
                        g = "该商品[" + f + "件起购]";
                        q = f;
                        p = q == j
                    }
                }
                break
        }
        if (g && g.length > 0 && q > 0) {
            n.val(q);
            yhdMiniCart.showWarningMsg(n, g)
        }
        return p
    };
    yhdMiniCart.showMinusTips = function(k, a, c, l, d, e) {
        if (c != 0) {
            return
        }
        var j = parseInt(k.attr("oriNum"));
        var b = jQuery("input[paramValue='" + l + "|" + d + "|" + (c == 0 ? 1 : 0) + "|" + e + "']");
        if (a < j && b.size() > 0) {
            var g = k.attr("limitNum");
            var h = g.split("|");
            var i = parseInt(h[1]);
            var f = "该商品【" + (j == i ? "每人" : "") + "限购" + j + "件】，优先减去" + chineseUrl + "价商品";
            yhdMiniCart.showWarningMsg(k, f)
        }
    };
    yhdMiniCart.calItemNum = function(c, e, b, a, g) {
        var f = parseInt(a);
        var d = jQuery("input[paramValue='" + c + "|" + e + "|" + (b == 0 ? 1 : 0) + "|" + g + "']");
        if (d.size() > 0) {
            f += parseInt(d.val())
        }
        return f
    };
    yhdMiniCart.showWarningMsg = function(d, c) {
        var b = d.offset().top;
        var a = '<span class="tips_arrow1">&#9670;</span>';
        a += '<span class="tips_arrow1 tips_arrow2">&#9670;</span>';
        a += "<p>" + c + "</p>";
        jQuery(".ap_shopping_warning").html(a);
        jQuery(".ap_shopping_warning").css("top", b - $("#miniCart").offset().top - $(".ap_shopping_warning").outerHeight() - 4).fadeIn(500);
        if (d.warningMsgHandler) {
            clearTimeout(d.warningMsgHandler)
        }
        d.warningMsgHandler = setTimeout(function() {
            jQuery(".ap_shopping_warning").fadeOut(500);
            d.warningMsgHandler = null
        }, 2000)
    };
    yhdMiniCart.bindMiniCartEvent = function() {
        if (currSiteId == 1) {
            yhdMiniCart.handCalSubTotal();
            yhdMiniCart.clickPlusCalSubTotal();
            yhdMiniCart.clickMinusCalSubTotal()
        }
    };
    yhdMiniCart.setMinusOperate = function(a, b) {
        if (b) {
            a.siblings("b:eq(1)").removeClass("minusDisable");
            a.siblings("b:eq(1)").addClass("minus")
        } else {
            if (!b) {
                a.siblings("b:eq(1)").removeClass("minus");
                a.siblings("b:eq(1)").addClass("minusDisable")
            }
        }
    };
    if (typeof currSiteId != "undefined" && currSiteId == 1) {
        var miniCartIfmByIE6 = function() {
            if ($.browser.msie && ($.browser.version == "6.0") && !$.support.style) {
                var a = $(".minicart_ifm").length;
                if (a == 0) {
                    $("<iframe class=minicart_ifm></iframe>").insertBefore(".minicart_list .list_detail:first")
                }
                var b = $(".minicart_list").find(".list_detail").first().height() + 2;
                $(".minicart_ifm").height(b)
            }
        };
        var removeIfmByIE6 = function() {
            if ($.browser.msie && ($.browser.version == "6.0")) {
                $(".minicart_ifm").remove()
            }
        }
    }
    var miniCart = function() {
        if ($.browser.msie && ($.browser.version == "6.0") && !$.support.style) {
            var b = $(".ap_shopping_warning").find("p").width();
            if (b > 140) {
                $(".ap_shopping_warning").find("p").width("140")
            }
        }
        var a = function() {
            if ($.browser.msie && ($.browser.version == "6.0") && !$.support.style) {
                var c = $(".minicart_list").find(".list_detail").first().find("ul").height();
                if (c > 345) {
                    $(".minicart_list").find(".list_detail").first().find("ul").height("345")
                } else {
                    if (c < 148) {
                        $(".minicart_list").find(".list_detail").first().find("ul").height("148")
                    }
                }
                miniCartIfmByIE6()
            }
        };
        $(".minicart_box").hover(function() {
            $(this).find(".minicart_list").show();
            a()
        }, function() {
            $(this).find(".minicart_list").hide();
            removeIfmByIE6()
        })
    }
}
function initAllMiniCart() {
    ymInitMiniCart()
}
function ymInitMiniCart() {
    buildCartNumber();
    miniCart();
    if (!jQuery("#miniCart").size()) {
        return
    }
    jQuery("#miniCart").data("inani", false).mouseover(function(a) {
        if (jQuery(this).data("inani")) {
            return
        }
        jQuery(this).data("inani", true);
        loadMiniCart()
    })
}
jQuery(document).ready(function() {
    if (isIndex != 1) {
        initAllMiniCart()
    }
});
(function(n) {
    var t = (typeof isSearchKeyWords != "undefined" && isSearchKeyWords == "1") ? 1 : 0;
    var m = (typeof isIndex != "undefined" && isIndex == 1) ? 1 : 0;
    var v = (typeof globalSearchSelectFlag != "undefined" && globalSearchSelectFlag == "0") ? 0 : 1;
    var u = n("#keyword");
    var o = n("#searchSuggest");
    var p = n("#fix_keyword");
    var x = n("#fix_searchSuggest");
    var q = n("#leaf");
    var w = window.loli || (window.loli = {});
    var r = w.app = w.app || {};
    var s = w.app.search = w.app.search || {};
    s.showHistory = function(d, a) {
        if (!v) {
            return
        }
        var f = q.size() > 0 ? q.val() : "0";
        var b = d.val();
        var c = d.attr("original");
        var e = function() {
            var h = URLPrefix.search + "/get_new_keywords.do?keyword=&leaf=" + f + "&flag=v1&hotSearchFlag=new&callback=?";
            n.getJSON(h, function(i) {
                if (i.ERROR) {
                    return
                } else {
                    a.html(i.value);
                    a.addClass("hd_search_history");
                    if (n.cookie("search_keyword_history") == "") {
                        n("#hd_clear_history_record", a).hide()
                    }
                    g(d, a);
                    a.show()
                }
            })
        };
        var g = function(i, h) {
            h.delegate("#hd_clear_history_record", "click", function() {
                var k = URLPrefix.sitedomain;
                var j = {path: "/",domain: k,expireDays: -1};
                n.cookie("search_keyword_history", "", j);
                n(this).hide();
                n(".hd_s_history dd", h).remove()
            })
        };
        if (b == "" || n.trim(b) == "" || n.trim(b) == "请输入关键词" || (n.trim(b) == c && !t)) {
            e()
        }
    };
    s.showSuggest = function(d, a) {
        if (!v) {
            return
        }
        var f = q.size() > 0 ? q.val() : "0";
        var b = d.val();
        var c = d.attr("original");
        var e = function() {
            var h = URLPrefix.search + "/get_new_keywords.do?keyword=" + encodeURIComponent(encodeURIComponent(b)) + "&leaf=" + f + "&flag=v1&hotSearchFlag=new&newSmartBoxFlag=new&callback=?";
            n.getJSON(h, function(i) {
                if (i.ERROR) {
                    return
                } else {
                    a.html(i.value);
                    a.removeClass("hd_search_history");
                    g(d, a);
                    a.show()
                }
            })
        };
        var g = function(i, h) {
            var j = h.find("ul>li");
            h.data("suggestLength", j.length);
            h.data("curSuggestIndex", -1);
            j.mouseenter(function() {
                var k = j.index(this);
                if (n(this).hasClass("haslist")) {
                    n(this).addClass("select_haslist").siblings().removeClass("select_haslist select");
                    h.children("ul").css("height", "336px")
                } else {
                    n(this).addClass("select").siblings().removeClass("select_haslist select");
                    h.children("ul").css("height", "")
                }
                h.data("curSuggestIndex", k)
            });
            j.mouseleave(function() {
                n(this).removeClass("select select_haslist")
            });
            n("#choose_list dd", h).live("mouseover", function() {
                n(this).find("#s_cart_btn").show();
                return false
            });
            n("#choose_list dd", h).live("mouseout", function() {
                n(this).find("#s_cart_btn").hide();
                return false
            });
            n("a[id=s_cart_btn]").hide()
        };
        if ((b != "" && n.trim(b) != "" && n.trim(b) != "请输入关键词" && n.trim(b) != c) || (n.trim(b) == c && t)) {
            e()
        }
    };
    s.registerGlobalEvent = function() {
        n("#site_header").find(".hd_search_wrap").bind("mouseleave", function() {
            o.hide()
        });
        n(document).bind("click", function(d) {
            var e = d.target;
            if (e.id == "hd_clear_history_record" || e.className == "keywordInput" || e.className == "fl") {
                return
            }
            o.hide();
            x.hide()
        });
        var c = function(h, i, d) {
            h = h || window.event;
            var f = h.keyCode;
            var j = d.find("ul>li");
            var k = j.length;
            var g = (d.data("curSuggestIndex") != null) ? d.data("curSuggestIndex") : -1;
            d.data("suggestLength", k);
            if (k > 0) {
                if (f == "38") {
                    if (g <= 0) {
                        g = k - 1
                    } else {
                        g = g - 1
                    }
                    d.data("curSuggestIndex", g)
                } else {
                    if (f == "40") {
                        if (g >= (k - 1)) {
                            g = 0
                        } else {
                            g = g + 1
                        }
                        d.data("curSuggestIndex", g)
                    }
                }
                if (f == "38" || f == "40") {
                    var e = j.eq(g);
                    if (e.hasClass("haslist")) {
                        e.addClass("select_haslist").siblings().removeClass("select_haslist select");
                        d.children("ul").css("height", "336px")
                    } else {
                        e.addClass("select").siblings().removeClass("select_haslist select");
                        d.children("ul").css("height", "")
                    }
                    if (e.attr("id")) {
                        i.val(j.eq(0).children("a").text());
                        if (e.attr("id") == "recom1") {
                            n("#recommendId", d).val(n("#recom1Id", d).val());
                            n("#recommendName", d).val(n("#recom1Name", d).val())
                        }
                        if (e.attr("id") == "recom2") {
                            n("#recommendId", d).val(n("#recom2Id", d).val());
                            n("#recommendName", d).val(n("#recom2Name", d).val())
                        }
                    } else {
                        i.val(e.children("a").text());
                        n("#recommendId", d).val("");
                        n("#recommendName", d).val("")
                    }
                    if (m) {
                        i.siblings("label").hide()
                    }
                }
                if (f == "13") {
                    var e = j.eq(g);
                    if (e.attr("id")) {
                        searchMe(i.val(), n("#recommendId", d).val(), n("#recommendName", d).val())
                    } else {
                        searchMe(i.val(), "0", "0")
                    }
                }
            } else {
                if (f == "13") {
                    searchMe(i.val(), "0", "0")
                }
            }
        };
        var a = function(f, i, d) {
            f = f || window.event;
            var e = f.keyCode;
            if (e == "116" || e == "16" || e == "17" || e == "38" || e == "40" || e == "13") {
                return
            }
            var g = i.val();
            var h = i.attr("original");
            if (g == "" || n.trim(g) == "" || n.trim(g) == "请输入关键词" || (n.trim(g) == h && !t)) {
                s.showHistory(i, d)
            } else {
                s.showSuggest(i, d)
            }
        };
        var b = function(h, k, f) {
            h = h || window.event;
            if (h) {
                var g = document.createElement("input").webkitSpeech === undefined;
                if (!g) {
                    var i = h.pageX;
                    var e = k.outerWidth();
                    var l = k.offset().left;
                    var j = l + e - 25;
                    var d = l + e;
                    if (i >= j && i <= d) {
                        return
                    }
                }
            }
            a(h, k, f)
        };
        u.keydown(function(d) {
            c(d, u, o)
        });
        p.keydown(function(d) {
            c(d, p, x)
        });
        u.keyup(function(d) {
            a(d, u, o)
        });
        p.keyup(function(d) {
            a(d, p, x)
        });
        u.click(function(d) {
            b(d, u, o)
        });
        p.click(function(d) {
            b(d, p, x)
        })
    };
    s.goSearchKeywords = function() {
    };
    s.loadHotKeywords = function() {
    };
    n(document).ready(function() {
        s.registerGlobalEvent()
    })
})(jQuery);
function findNames() {
}
function _goSearch() {
}
function goSearch() {
}
function findNamesByDiv() {
}
function _goSearchByDiv() {
}
function goSearchByDiv() {
}
function loadComplete_findNames() {
}
function searchListHover() {
}
function clearRecord() {
}
function roll() {
}
function hotKeywords_onDocumentReady() {
}
function reloadKeyWordsData() {
}
function searchRecommend(b) {
    if (b != null && b != "") {
        window.location = b
    }
}
function cutString(d, c) {
    if (d == null || d.length <= c) {
        return d
    }
    return d.substring(0, c)
}
function addKeywordHistory(b) {
    if (typeof (b) == "undefined") {
        return
    }
    b = jQuery.trim(b);
    b = b.replace(/[,]/g, " ");
    b = b.replace(/[，]/g, " ")
}
function selectSearchCategory(c, d) {
    jQuery("#searchCategory").html(d);
    jQuery("#leaf").val("0_" + c)
}
function emptySearchBar(j) {
    if (!j) {
        j = "#keyword"
    }
    var i = jQuery(j);
    var h = i.parent("div").find("label");
    var g = i.attr("original");
    var f = i.val();
    if (i.val() != "" && h.size() > 0) {
        h.hide();
        i.trigger("click");
        return
    }
    if (f.indexOf(g) == 0) {
        i.val(f.substring(g.length));
        i.css("color", "#333333")
    }
    if (i.val() != "") {
        i.trigger("click")
    }
}
function searchMe(z, p, q) {
    var x = null;
    var s = document.getElementById("recommendId");
    if (s) {
        x = s.value
    }
    var o = null;
    var v = document.getElementById("recommendName");
    if (v) {
        o = v.value
    }
    var t = jQuery("#keyword");
    if (!z) {
        z = t.val()
    } else {
        if (z instanceof jQuery) {
            t = z;
            z = t.val()
        }
    }
    if (z != null && z != "") {
        var r = t.attr("original");
        if (r != null && r != "" && r != "请输入关键词") {
            if (r == z) {
                var u = t.attr("url");
                if (u != null && u != "") {
                    window.location = u;
                    return
                }
            }
        }
    } else {
        if ((isIndex == 1 && (typeof (indexFlag) != "undefined" && typeof (indexFlag) == "number" && indexFlag == 1))) {
            var w = t.parent("div").find("label")[0];
            if (w && (jQuery(w).css("display") == "block" || jQuery(w).css("display") == "inline")) {
                var u = t.attr("url");
                if (u != null && u != "") {
                    window.location = u;
                    return
                }
            }
        }
    }
    if (z != null && z != "" && z != "请输入关键词") {
        addKeywordHistory(z)
    } else {
        return
    }
    z = z.replace(/\//gi, " ");
    var y = "0";
    if (jQuery("#leaf").size() > 0) {
        y = jQuery("#leaf").val()
    }
    var n = jQuery.cookie("provinceId");
    if (p != null && p != "0") {
        window.location = URLPrefix.search_keyword + "/s2/c" + p + "-" + q + "/k" + encodeURIComponent(encodeURIComponent(z)) + "/" + n + "/"
    } else {
        if (x != null && x != "") {
            window.location = URLPrefix.search_keyword + "/s2/c" + x + "-" + o + "/k" + encodeURIComponent(encodeURIComponent(z)) + "/" + n + "/"
        } else {
            window.location = URLPrefix.search_keyword + "/s2/c" + y + "-0/k" + encodeURIComponent(encodeURIComponent(z)) + "/" + n + "/"
        }
    }
}
function searchMeForBrand() {
    var d = jQuery("#keyword");
    var c = d.val();
    if (c == "" || c == "商品、品牌") {
        return
    }
    searchMe()
}
function readAdv_hotKeywords_onDocumentReady(e) {
    var g = jQuery("#keyword");
    if (e) {
        g = jQuery(e)
    }
    if (!g[0]) {
        return
    }
    var h = g.attr("original");
    if (h == null || h == "") {
        h = "请输入关键词"
    }
    var f = g.val();
    if (f == h || f == "" || f == "请输入关键词") {
        g.css("color", "#999999");
        g.bind("focus", function() {
            if (this.value == h) {
                this.value = "";
                this.style.color = "#333333"
            }
        }).bind("blur", function() {
            if (this.value == "") {
                this.value = h;
                this.style.color = "#999999"
            }
        })
    }
}
function indexReadAdv_hotKeywords_onDocumentReady() {
    var f = jQuery("#keyword").attr("original");
    if (f == null || f == "") {
        f = "请输入关键词"
    }
    var e = jQuery("#keyword").val();
    var d = jQuery("#keyword").parent("div").find("label");
    if (!d[0]) {
        return
    }
    if (e == f || e == "") {
        d.css({display: "block"});
        jQuery("#keyword").css("color", "#333333")
    }
    jQuery("#keyword").bind("focus", function() {
        d.css({color: "#CCCCCC"});
        if (this.value == f) {
            this.style.color = "#CCCCCC"
        } else {
            this.style.color = "#333333"
        }
    }).bind("blur", function() {
        if (this.value == "" || this.value == f || this.value == "请输入关键词") {
            d.css({color: "#666666",display: "block"});
            jQuery("#keyword").val("")
        }
    }).bind("keydown", function() {
        if (this.value == "" || this.value == f) {
            d.hide()
        }
    })
}
function searchFocus(e) {
    var f = $("#keyword");
    if (e) {
        f = $(e)
    }
    if ((isIndex == 1 && (typeof (indexFlag) != "undefined" && typeof (indexFlag) == "number" && indexFlag == 1))) {
        return
    }
    var d = f.val();
    if (d == null || d == "") {
        f.val("请输入关键词");
        d = "请输入关键词"
    }
    if (d == "请输入关键词") {
        f.css("color", "#999999")
    }
    f.focus(function() {
        var a = $(this);
        if (a.val() == "请输入关键词") {
            a.val("").css("color", "#333")
        }
    }).blur(function() {
        if (!$(this).val().replace(/\s/gi, "")) {
        }
    })
}
function searchKeywords_onDocumentReady(v) {
    var o = jQuery("#keyword");
    if (v) {
        o = jQuery(v)
    }
    if (!o[0]) {
        return
    }
    var t = o.val();
    if ((isIndex == 1 && (typeof (indexFlag) != "undefined" && typeof (indexFlag) == "number" && indexFlag == 1)) || (typeof (isMallIndex) != "undefined" && isMallIndex == 1) || (typeof (_globalIsUseAdHotWords) != "undefined" && _globalIsUseAdHotWords == false)) {
        if ((typeof (isMallIndex) != "undefined" && isMallIndex == 1) || (typeof (_globalIsUseAdHotWords) != "undefined" && _globalIsUseAdHotWords == false)) {
            readAdv_hotKeywords_onDocumentReady(v)
        } else {
            indexReadAdv_hotKeywords_onDocumentReady()
        }
        t = ""
    } else {
        if (isIndex != 1 || jQuery.trim(o.attr("url")) != "") {
            readAdv_hotKeywords_onDocumentReady(v)
        }
    }
    var u = 1;
    var m = 1;
    var n = URLPrefix.search_keyword + "/recommend/headHotKeywordRecommend.do?threshold=10&mcSiteId=" + u + "&siteType=" + m;
    if (typeof (t) != "undefined" && t != "") {
        n += "&keyword=" + encodeURIComponent(encodeURIComponent(t))
    }
    var e = jQuery("#curCategoryIdToGlobal").val();
    if (typeof (e) != "undefined") {
        n += "&categoryId=" + e
    }
    var r = false;
    var q = jQuery("#hotKeywordsShow").data("data-isLoaded");
    if (q == "1") {
        return
    }
    jQuery("#hotKeywordsShow").data("data-isLoaded", "1");
    var p = function(c) {
        if (isIndex == 1) {
            var b = $("#hotKeywordsShow").attr("data-specialHotword");
            var a = (typeof globalSpecialHotwordFlag != "undefined" && globalSpecialHotwordFlag == "0") ? 0 : 1;
            if (a && b) {
                var d = $.parseJSON(b);
                if (d && d.text && d.linkUrl) {
                    var f = "<a title='" + d.text + "' href='" + d.linkUrl + "' target='_blank' data-ref='" + d.perTracker + "'>" + d.text + "</a>";
                    c = f + c
                }
            }
        }
        return c
    };
    try {
        jQuery.ajax({url: n,dataType: "jsonp",jsonp: "callback",jsonpCallback: "keywordRecommendCallback",timeout: 3000,success: function(f) {
                if (r) {
                    return
                }
                if (f == null || f.headhotkeywords == null || f.headhotkeywords.length < 1) {
                    jQuery("#hotKeywordsShow > a").remove();
                    getHotkeywordHtml()
                } else {
                    var a = f.headhotkeywords;
                    var b = [];
                    for (var h = 0; h < a.length; h++) {
                        var d = a[h];
                        var c = URLPrefix.search_keyword + "/s2/c0-0/k" + encodeURIComponent(d) + "/" + jQuery.cookie("provinceId") + "/";
                        d = '<a title="' + d + '" target="_blank" href="' + c + '" data-ref="shkw_' + encodeURIComponent(d) + '">' + d + "</a>";
                        b.push(d)
                    }
                    var g = p(b.join(" "));
                    jQuery("#hotKeywordsShow > a").remove();
                    jQuery("#hotKeywordsShow").append(g);
                    jQuery("#hotKeywordsShow").data("data-searchKeyLoaded", "1");
                    r = true
                }
            },error: function() {
                jQuery("#hotKeywordsShow > a").remove();
                getHotkeywordHtml();
                r = true
            }})
    } catch (s) {
        jQuery("#hotKeywordsShow > a").remove();
        getHotkeywordHtml();
        r = true
    }
    setTimeout(function() {
        if (!r) {
            jQuery("#hotKeywordsShow").data("data-isLoadFinish", "true");
            jQuery("#hotKeywordsShow > a").remove();
            getHotkeywordHtml()
        }
    }, 3000)
}
function getHotkeywordHtml() {
    var hotkeywordsArray = [];
    var hotkeywordsList = jQuery("#hotKeywordsShow").attr("data-grid");
    if (!hotkeywordsList) {
        return
    }
    hotkeywordsList = eval(hotkeywordsList);
    if (!jQuery.isArray(hotkeywordsList)) {
        return
    }
    if (hotkeywordsList.length < 1) {
        return
    }
    for (var i = 0; i < hotkeywordsList.length; i++) {
        var hotkeyword = hotkeywordsList[i];
        var text = "";
        if (hotkeyword == null || hotkeyword.text == null || hotkeyword.text == "") {
            continue
        } else {
            text = hotkeyword.text
        }
        var classStyle = "";
        if (hotkeyword.style && hotkeyword.style == 5) {
            classStyle = 'class="hot_link_red"'
        }
        hotkeyword = '<a title="' + text + '" ' + classStyle + ' target="_blank" href="' + hotkeyword.linkUrl + '" data-ref="' + hotkeyword.perTracker + '">' + text.substring(0, 10) + "</a>";
        hotkeywordsArray.push(hotkeyword)
    }
    jQuery("#hotKeywordsShow").append(hotkeywordsArray.join(" "))
}
jQuery(document).ready(function() {
    if (isIndex != 1) {
        if (jQuery("#hotKeywordsShow")[0]) {
            searchKeywords_onDocumentReady();
            if (typeof isFixTopNav != "undefined" && isFixTopNav == true) {
                readAdv_hotKeywords_onDocumentReady("#fix_keyword")
            }
        }
    }
    if (isIndex == 1 && jQuery.trim(jQuery("#fix_keyword").attr("url")) != "") {
        if (typeof isFixTopNav != "undefined" && isFixTopNav == true) {
            readAdv_hotKeywords_onDocumentReady("#fix_keyword")
        }
    }
    searchFocus();
    if (typeof isFixTopNav != "undefined" && isFixTopNav == true) {
        searchFocus("#fix_keyword")
    }
});
(function(a) {
    a.fn.bgIframe = a.fn.bgiframe = function(c) {
        if (a.browser.msie && parseInt(a.browser.version) <= 6) {
            c = a.extend({top: "auto",left: "auto",width: "auto",height: "auto",opacity: true,src: "javascript:false;"}, c || {});
            var d = function(e) {
                return e && e.constructor == Number ? e + "px" : e
            }, b = '<iframe class="bgiframe"frameborder="0"tabindex="-1"src="' + c.src + '"style="display:block;position:absolute;z-index:-1;' + (c.opacity !== false ? "filter:Alpha(Opacity='0');" : "") + "top:" + (c.top == "auto" ? "expression(((parseInt(this.parentNode.currentStyle.borderTopWidth)||0)*-1)+'px')" : d(c.top)) + ";left:" + (c.left == "auto" ? "expression(((parseInt(this.parentNode.currentStyle.borderLeftWidth)||0)*-1)+'px')" : d(c.left)) + ";width:" + (c.width == "auto" ? "expression(this.parentNode.offsetWidth+'px')" : d(c.width)) + ";height:" + (c.height == "auto" ? "expression(this.parentNode.offsetHeight+'px')" : d(c.height)) + ';"/>';
            return this.each(function() {
                if (a("> iframe.bgiframe", this).length == 0) {
                    this.insertBefore(document.createElement(b), this.firstChild)
                }
            })
        }
        return this
    };
    if (!a.browser.version) {
        a.browser.version = navigator.userAgent.toLowerCase().match(/.+(?:rv|it|ra|ie)[\/: ]([\d.]+)/)[1]
    }
})(jQuery);
var Class = {create: function() {
        return function() {
            this.initialize.apply(this, arguments)
        }
    }};
var Extend = function(f, d) {
    for (var e in d) {
        f[e] = d[e]
    }
};
function stopDefault(b) {
    if (b && b.preventDefault) {
        b.preventDefault()
    } else {
        window.event.returnValue = false
    }
    return false
}
var Stars = Class.create();
Stars.prototype = {initialize: function(c, r) {
        this.SetOptions(r);
        var x = 999;
        var p = (document.all) ? true : false;
        var v = document.getElementById(c).getElementsByTagName("a");
        var u = document.getElementById(this.options.Input) || document.getElementById(c + "-input");
        var y = document.getElementById(this.options.Tips) || document.getElementById(c + "-tips");
        var q = " " + this.options.nowClass;
        var s = this.options.tipsTxt;
        var z = v.length;
        for (i = 0; i < z; i++) {
            v[i].value = i;
            v[i].onclick = function(a) {
                stopDefault(a);
                this.className = this.className + q;
                x = this.value;
                u.value = this.getAttribute("star:value");
                y.innerHTML = s[this.value]
            };
            v[i].onmouseover = function() {
                if (x < 999) {
                    var a = RegExp(q, "g");
                    v[x].className = v[x].className.replace(a, "")
                }
            };
            v[i].onmouseout = function() {
                if (x < 999) {
                    v[x].className = v[x].className + q
                }
            }
        }
        if (p) {
            var t = document.getElementById(c).getElementsByTagName("li");
            for (var i = 0, z = t.length; i < z; i++) {
                var w = t[i];
                if (w) {
                    w.className = w.getElementsByTagName("a")[0].className
                }
            }
        }
    },SetOptions: function(b) {
        this.options = {Input: "",Tips: "",nowClass: "current-rating",tipsTxt: ["1分-很不满意", "2分-不满意", "3分-一般", "4分-满意", "5分-非常满意"]};
        Extend(this.options, b || {})
    }};
function setHomepage() {
    if (document.all) {
        document.body.style.behavior = "url(#default#homepage)";
        document.body.setHomePage(httpUrl)
    } else {
        if (window.sidebar) {
            if (window.netscape) {
                try {
                    netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect")
                } catch (b) {
                    alert("该操作被浏览器拒绝，如果想启用该功能，请在地址栏内输入 about:config,然后将项 signed.applets.codebase_principal_support 值该为true")
                }
            }
            var a = Components.classes["@mozilla.org/preferences-service;1"].getService(Components.interfaces.nsIPrefBranch);
            a.setCharPref("browser.startup.homepage", httpUrl)
        }
    }
}
function globalLogoff() {
}
function bookmark() {
    var c;
    var a = /^http{1}s{0,1}:\/\/([a-z0-9_\\-]+\.)+(yihaodian|1mall|111|yhd){1}\.(com|com\.cn){1}\?(.+)+$/;
    if (a.test(httpUrl)) {
        c = "&ref=favorite"
    } else {
        c = "?ref=favorite"
    }
    var d = httpUrl + c;
    if ($.browser.msie && ($.browser.version == "6.0") && !$.support.style) {
        d = httpUrl
    }
    try {
        if (document.all) {
            window.external.AddFavorite(d, favorite)
        } else {
            try {
                window.sidebar.addPanel(favorite, d, "")
            } catch (b) {
                alert("抱歉，您所使用的浏览器无法完成此操作。\n\n加入收藏失败，请使用Ctrl+D进行添加")
            }
        }
    } catch (b) {
        alert("抱歉，您所使用的浏览器无法完成此操作。\n\n加入收藏失败，请使用Ctrl+D进行添加")
    }
}
var myCartTopHeaderTimer;
function clearMyCartTopHeaderTimer() {
    if (myCartTopHeaderTimer != null) {
        clearTimeout(myCartTopHeaderTimer)
    }
}
function buildMyYihaodian() {
    jQuery("#myYihaodian").mouseover(function(a) {
        clearMyCartTopHeaderTimer();
        jQuery("#myYihaodianFloatDiv").show();
        a.stopPropagation()
    });
    jQuery("#myYihaodianFloatDiv").mouseout(function(a) {
        clearMyCartTopHeaderTimer();
        myCartTopHeaderTimer = setTimeout(function() {
            jQuery("#myYihaodianFloatDiv").hide()
        }, 1000);
        a.stopPropagation()
    }).mouseover(function(a) {
        clearMyCartTopHeaderTimer();
        a.stopPropagation()
    });
    jQuery("body").click(function(a) {
        clearMyCartTopHeaderTimer();
        jQuery("#myYihaodianFloatDiv").hide();
        a.stopPropagation()
    })
}
var hasPingAnCookie = 0;
function writeHeaderContent() {
    var c = jQuery.cookie("ucocode");
    if (c && c == "pingan") {
        hasPingAnCookie = 1
    }
    if (jQuery("#global_top_bar")[0]) {
        loli.globalCheckLogin(globalInitYhdLoginInfo);
        return
    }
    var e = jQuery.cookie("ut");
    var i = jQuery.cookie("uname");
    var a = 0;
    if (e) {
        a = 1
    }
    if (document.domain.indexOf("111.com", 0) == -1) {
        if (i) {
            i = decodeURIComponent(i);
            i = cutUsername(i);
            if (i == null) {
                i = ""
            }
            var b = jQuery("#user_name_info");
            if (b.length == 0) {
                if (a) {
                    jQuery("#user_name").text(i)
                }
            } else {
                jQuery("#user_name_info").text(i);
                jQuery("#user_name_welcome").show()
            }
        }
    }
    if (a == 1) {
        if (document.domain.indexOf("111.com", 0) != -1) {
            if (i) {
                i = decodeURIComponent(i);
                if (i == null) {
                    i = ""
                }
                jQuery("#user_name").text(i)
            }
        }
        jQuery("#login").hide();
        jQuery("#logout").show()
    }
    var h = "";
    var f = jQuery.cookie("ucocode");
    var d = jQuery.cookie("externaluserlevel");
    if ((f && f == "pingan")) {
        hasPingAnCookie = 1;
        h = "尊敬的万里通会员";
        jQuery(".provincebox").addClass("provincebox2")
    } else {
        if (f && f == "tencent") {
            if (d && d > 0) {
                h = "尊敬的QQ会员"
            } else {
                if (d && d == 0) {
                    h = "尊敬的QQ用户"
                }
            }
        } else {
            if (f && f == "kaixin001" && jQuery("#KX_JS_URL").size() > 0) {
                if (jQuery("#kx001_btn_login").parent().size() > 0) {
                    jQuery("#logout").hide();
                    jQuery("#kx001_btn_login").parent().show()
                }
                var g = jQuery("#KX_JS_URL").val();
                jQuery.getScript(g, function() {
                    if (jQuery("#kx001_btn_login").size() > 0 && jQuery("#kx001_btn_login").html() == "") {
                        try {
                            KX001.init("974091834200c72a39a7bb394900fb0c", "/pages/kaixin/kx001_receiver.html")
                        } catch (j) {
                        }
                    }
                })
            }
        }
    }
    if (h && h != "") {
        if (i == null) {
            i = ""
        }
        if (currSiteId == 1) {
            var b = $("#user_name").find("#user_name_info");
            if (b.length == 0) {
                if (a) {
                    jQuery("#user_name").text(cutUsername(h + i))
                }
            } else {
                jQuery("#user_name_info").text(i);
                jQuery("#user_name_welcome").show()
            }
        } else {
            jQuery("#user_name").text(h + i)
        }
    }
}
function globalInitYhdLoginInfo(a) {
    if (a && a.result && a.userName) {
        var b = a.result;
        var h = a.userName;
        var g = jQuery("#global_login");
        var f = jQuery("#global_unlogin");
        var d = jQuery("#logout");
        if (b == "1") {
            g.show();
            d.show();
            f.hide();
            var c = jQuery.cookie("uname");
            if (c && jQuery.trim(c) != "") {
                jQuery("#user_name").text(c)
            } else {
                jQuery("#user_name").text(h)
            }
            if (a.memberGrade) {
                var e = a.memberGrade;
                if (e == "1" || e == "2" || e == "3") {
                    jQuery("#global_member_grade").removeClass("hd_vip0").addClass("hd_vip" + e)
                }
            }
        }
    }
}
function cutUsername(a) {
    return a
}
function bothSiteLogoutJsonp() {
    var c = false;
    var b = false;
    var a = URLPrefix.passport;
    jQuery.getJSON(a + "/passport/logoutJsonp.do?timestamp=" + new Date() + "&callback=?", function(d) {
        if (d && d.code == "0") {
            c = true
        }
        location.href = currDomain
    });
    if (myCartTopHeaderTimer) {
        clearMyCartTopHeaderTimer()
    }
    myCartTopHeaderTimer = setTimeout(function() {
        if (!(c && b)) {
            window.location.href = currDomain
        }
    }, 3000)
}
function pingan_quit() {
    var a = new Date((new Date()).getTime()).toGMTString();
    document.cookie = "ut=;expires=" + a + ";domain=." + no3wUrl + ";path=/";
    document.cookie = "ucocode=;expires=" + a + ";domain=." + no3wUrl + ";path=/";
    document.cookie = "cocode=;expires=" + a + ";domain=." + no3wUrl + ";path=/";
    location.href = "https://www.wanlitong.com/eloyalty_chs/start.swe?SWENeedContext=false&SWECmd=Logoff&SWEC=2&SWEBID=-1&SWETS="
}
function kx001_onlogout() {
    window.location.href = httpUrl + "/passport/logoff.do"
}
function hightLightMenu(c, b) {
    var d = jQuery(c);
    var a = location.href;
    d.each(function(j) {
        if (j == 0) {
            return true
        }
        var h = jQuery(this).find("a");
        var g = h.attr("href");
        var f = h.attr("hl");
        var e = false;
        e = (a.indexOf(g) > -1);
        if (!e) {
            if (f) {
                e = (a.indexOf(f) > -1)
            }
        }
        if (!e) {
            e = (a.indexOf("point2channel.do") > -1) && (g.indexOf("/point2/pointIndex.do") > -1)
        }
        if (e) {
            if (j) {
                if (currSiteId == 2) {
                    d.eq(0).addClass("removehome");
                    h.addClass("select")
                } else {
                    if (f != null && f.length > 0) {
                        d.eq(0).removeClass("cur");
                        h.parent().addClass("cur")
                    }
                }
            }
            return false
        }
    })
}
function initHeader() {
    jQuery(".top_bar_link > ul > li").hover(function() {
        jQuery(this).children("ul").show().end();
        jQuery(this).find(".qixia").addClass("qixia_hover")
    }, function() {
        jQuery(this).children("ul").hide().end();
        jQuery(this).find(".qixia").removeClass("qixia_hover")
    });
    try {
        writeHeaderContent()
    } catch (a) {
    }
    hightLightMenu("#global_menu li", null)
}
function lazyLoadBottomBrandsData() {
    var a = function() {
        var c = jQuery("#bottomBrand");
        if (!c.size()) {
            return
        }
        var b = document.documentElement.clientHeight + Math.max(document.documentElement.scrollTop, document.body.scrollTop);
        if (c.offset().top > b + 100 || c.data("loaded")) {
            return
        } else {
            c.data("loaded", true)
        }
        c.html("<p align='center'><img src='" + imagePath + "/loade.gif'/>请您稍等,数据正在加载...</p>");
        jQuery.getJSON(URLPrefix.central + "/bottomBrand/ajaxGetBottomBrandsData.do?callback=?", function(f) {
            c.html("");
            if (f.value) {
                var e = jQuery(f.value).find("ul");
                var d = "";
                e.each(function() {
                    d += jQuery(this).html()
                });
                c.html(d)
            }
            jQuery(window).unbind("scroll", a)
        })
    };
    jQuery(window).bind("scroll", a);
    a()
}
function headNavFixed() {
    if (typeof currSiteId != "undefined" && currSiteId == 1) {
        function a() {
            var b = $("#headerNav").offset().top;
            var c = jQuery("#headerNav");
            jQuery(window).scroll(function() {
                var d = $(this).scrollTop();
                if (d > b) {
                    c.addClass("hd_nav_fixed");
                    if (jQuery("#headerNav_box").length == 0) {
                        c.after('<p class="headerNav_box" id="headerNav_box"></p>')
                    }
                } else {
                    jQuery("#headerNav_box").remove();
                    c.removeClass("hd_nav_fixed");
                    jQuery("#fix_keyword").blur()
                }
            });
            if ($.browser.msie && ($.browser.version == "6.0") && !$.support.style) {
                var b = $("#headerNav").offset().top;
                $(window).scroll(function() {
                    var d = $(this).scrollTop();
                    if (d > b) {
                        c.addClass("hd_fixed_ie6");
                        var e = $("#headerNav_ifm").length;
                        if (e == 0) {
                            $('<iframe class=headerNav_ifm id="headerNav_ifm"></iframe>').insertBefore("#headerNav .wrap")
                        }
                        c.css("top", d)
                    } else {
                        c.removeClass("hd_fixed_ie6");
                        $("#headerNav_ifm").remove();
                        c.css("top", "0px");
                        jQuery("#fix_keyword").blur()
                    }
                })
            }
        }
        a()
    }
}
var yhdToolKit = window.yhdToolKit = window.yhdToolKit || {};
yhdToolKit.loadMobileAdv = function() {
    if (typeof isWidescreen != "undefined" && isWidescreen == true) {
        var a = null;
        var b = $("#glKeHuDuan");
        b.show();
        b.hover(function() {
            if (a != null) {
                clearTimeout(a)
            }
            a = setTimeout(function() {
                $("#glKeHuDuan").addClass("hd_mobile_hover")
            }, 200)
        }, function() {
            if (a != null) {
                clearTimeout(a)
            }
            $("#glKeHuDuan").removeClass("hd_mobile_hover")
        });
        b.find(".hd_iphone,.hd_ipad,.hd_android").click(function() {
            var c = $(this).attr("tk");
            gotracker("2", c)
        });
        $("#glKeHuDuan").lazyImg()
    }
    if (typeof isWidescreen != "undefined" && isWidescreen == true) {
        var a = null;
        var b = $("#hd_mobile_buy_wrap");
        b.show();
        b.hover(function() {
            if (a != null) {
                clearTimeout(a)
            }
            a = setTimeout(function() {
                $("#hd_mobile_buy_wrap").addClass("hd_mobile_buy_hover")
            }, 200)
        }, function() {
            if (a != null) {
                clearTimeout(a)
            }
            $("#hd_mobile_buy_wrap").removeClass("hd_mobile_buy_hover")
        });
        $("#hd_mobile_buy_wrap").lazyImg()
    }
};
yhdToolKit.getProductPicByDefaultPic = function(d, e, a) {
    try {
        if (d) {
            e = e > 1000 ? 1000 : e;
            a = a > 1000 ? 1000 : a;
            var b = d.lastIndexOf(".");
            return d.substring(0, b) + "_" + e + "x" + a + ".jpg"
        } else {
            var c = 115;
            if (e < 80) {
                c = 40
            } else {
                if (e > 150) {
                    c = 200
                }
            }
            return "http://image.yihaodianimg.com/statics/global/images/defaultproduct_" + c + "x" + c + ".jpg"
        }
    } catch (f) {
        return "http://image.yihaodianimg.com/statics/global/images/defaultproduct_115x115.jpg"
    }
};
jQuery(document).ready(function() {
    if (isIndex != 1) {
        initHeader()
    }
    lazyLoadBottomBrandsData();
    if (typeof isFixTopNav != "undefined" && isFixTopNav == true) {
        headNavFixed()
    }
    if (typeof currSiteId != "undefined" && currSiteId == 1) {
        jQuery("#footerServiceLinkId").lazyDom({load: false,flushPrice: false,indexLoad: true,callback: function() {
                addTrackerToEvent("#bottomHelpLinkId")
            }});
        jQuery("#bottom_footerCategory").lazyDom({load: false,flushPrice: false,indexLoad: true});
        addTrackerToEvent("#footer")
    }
    yhdToolKit.loadMobileAdv();
    var a = $("#global_right_pic").find("img");
    a.attr("src", a.attr("original"))
});
function newTopslider(q) {
    var p = jQuery(q);
    if (p.length < 1) {
        return
    }
    var o = null;
    var r = jQuery("#site_header");
    var k = r.css("padding-top");
    if (k && k.indexOf("px") >= 0) {
        k = k.replace("px", "")
    }
    if (r.attr("data-hfix")) {
        o = r.attr("data-hfix")
    }
    var n = p.find(".index_topbanner_fold");
    var m = p.find(".big_topbanner");
    var t = jQuery("#smallTopBanner");
    n.toggle(function() {
        $(this).removeClass("index_topbanner_unfold");
        $(this).html("展开<s></s>");
        m.slideUp();
        t.slideDown();
        if (o) {
            r.animate({"padding-top": k + "px"})
        }
    }, function() {
        $(this).addClass("index_topbanner_unfold");
        $(this).html("收起<s></s>");
        m.slideDown();
        t.slideUp();
        if (o) {
            r.animate({"padding-top": (k - o) + "px"})
        }
    });
    var l = m.find("img");
    l.attr("src", l.attr(isWidescreen ? "wideimg" : "shortimg")).removeAttr("wideimg").removeAttr("shortimg");
    l.eq(0).load(function() {
        var a = window.navigator.userAgent.toLowerCase();
        var d = /msie ([\d\.]+)/;
        if (d.test(a)) {
            var b = parseInt(d.exec(a)[1]);
            if (b <= 6) {
                var c = $(this).height();
                if (c > 450) {
                    $(this).css("height", 450)
                }
            }
        }
        if (o) {
            r.animate({"padding-top": (k - o) + "px"})
        }
        p.slideDown();
        lamuSlidUpAuto(n)
    });
    var s = t.find("img");
    s.each(function(b, a) {
        jQuery(a).attr("src", jQuery(a).attr(isWidescreen ? "wideimg" : "shortimg")).removeAttr("wideimg").removeAttr("shortimg")
    })
}
function lamuSlidUpAuto(d) {
    var e = function() {
        d.trigger("click")
    };
    var f = setTimeout(e, 5000);
    d.click(function() {
        clearInterval(f)
    })
}
;
runfunctions([], [initHeader, initProvince, initAllMiniCart, searchKeywords_onDocumentReady], this);
function runfunctions(i, j, h) {
    if (!(j && j.length)) {
        return
    }
    h = h || window;
    var f = j.shift();
    var g = i.shift() || [];
    for (; ; f = j.pop(), g = i.pop()) {
        if (typeof f == "function") {
            setTimeout(function() {
                try {
                    f.apply(h, g)
                } catch (a) {
                }
                runfunctions(i, j, h)
            }, 100);
            return false
        }
    }
}
;
(function(b) {
    YHD.HomePage = new function() {
        this.init = function() {
            newTopslider("#topCurtain");
            h("#hd_head_skin");
            $("body").delegate("a", "click", function() {
                $(this).css("outline", "none")
            })
        };
        function h(e) {
            var c = b(e);
            if (c.length > 0) {
                var d = c.attr(isWidescreen ? "data-wiData" : "data-siData"), l = [];
                $.each($.parseJSON(d), function(n, k) {
                    l.push('<div style="background: url(&quot;' + k.url + "&quot;) no-repeat scroll center top; height: " + k.height + 'px;"></div>')
                });
                if (l.length > 0) {
                    c.prepend(l.join(""))
                }
            }
        }
        function g(c) {
            $(c + " a[tk]").each(function(e) {
                var d = this;
                $(d).click(function() {
                    addTrackPositionToCookie("1", $(d).attr("tk"))
                })
            })
        }
        function a() {
            if (!b.cookie("provinceId")) {
                return
            }
            var c = b("#loucengBanner");
            if (c.data("data-isLoad")) {
                return
            }
            c.data("data-isLoad", true);
            var d = b.cookie("provinceId");
            var e = currSiteId || 1;
            var p = currSiteType || 1;
            var o = isWidescreen ? 1 : 0;
            var n = URLPrefix.pms + "/interface/getCmsBannerInHomePage.do";
            n = n + "?provinceId=" + d + "&currSiteId=" + e + "&currSiteType=" + p + "&widescreen=" + o;
            b.getJSON(n + "&callback=?", function(w) {
                if (w == null) {
                    return
                }
                var y = w.success, x = b("#loucengBanner"), k = b("#loucengBanner_textarea");
                if (y == 1) {
                    if (YHD.HomePage.Tools.needReadPms()) {
                        var l = w.info;
                        if (l && l.length > 0) {
                            var m = l[0];
                            var z = [];
                            z.push('<a href="' + m.jumpUrl + '" target="_blank" title="' + m.title + '" data-ref="' + m.perTracker + '">');
                            z.push('<img src="' + m.img + '" >');
                            z.push("</a>");
                            c.html(z.join(""))
                        }
                    } else {
                        v()
                    }
                } else {
                    v()
                }
                function v() {
                    if (k.length > 0) {
                        x.html(k.val());
                        var q = x.find("img");
                        b.each(q, function(r, t) {
                            var u = b(t);
                            var s = isWidescreen ? "wideimg" : "sideimg";
                            if (u.attr(s)) {
                                u.attr("src", u.attr(s));
                                u.removeAttr(s)
                            }
                        })
                    }
                }
            })
        }
        var f = function(n, o, d, p) {
            if (!(n || o || temp)) {
                return
            }
            var e = $(n);
            var c = function() {
                $.ajax({url: o,dataType: "jsonp",success: function(k) {
                        var l = d(k);
                        e.html(l);
                        getAjaxProductPrice(n)
                    }})
            };
            if (p) {
                setTimeout(function() {
                    c()
                }, p)
            } else {
                c()
            }
        }
    }
})(jQuery);
function getAjaxProductPrice(f) {
    if (!jQuery.cookie("provinceId")) {
        return
    }
    var g = URLPrefix.busystock ? URLPrefix.busystock : "http://gps.yihaodian.com";
    var h = "?mcsite=" + currBsSiteId + "&provinceId=" + jQuery.cookie("provinceId");
    var k = $(f).find("[productid]");
    jQuery.each(k, function(c, a) {
        var b = $(a).attr("productid");
        if (b != null && b != "") {
            h += "&productIds=" + b
        }
    });
    var l = g + "/busystock/restful/truestock";
    jQuery.getJSON(l + h + "&callback=?", function(a) {
        if (a == null || a == "") {
            return
        }
        jQuery.each(a, function(d, b) {
            var e = $(f).find("[productid='" + b.productId + "']");
            if (e) {
                if (globalShowMarketPrice == 1) {
                    var c = "<span>¥</span><strong>" + b.productPrice + "</strong>";
                    c += "<del>¥" + b.marketPrice + "</del>";
                    e.html(c).removeAttr("productid")
                } else {
                    var c = "<span>¥</span><strong>" + b.productPrice + "</strong>";
                    if (b.curPriceType && b.curPriceType == 2 && b.yhdPrice) {
                        c += "<del>¥" + b.yhdPrice + "</del>"
                    }
                    e.html(c).removeAttr("productid")
                }
            }
        })
    })
}
function scrollToTop() {
    var c = $(".fixedRight"), d = c.find(".toTop");
    loli.delay(window, "scroll", null, function() {
        if ($(window).scrollTop() > 0) {
            d.css("display", "block")
        } else {
            d.css("display", "none")
        }
        if ($.browser.msie && $.browser.version <= 6) {
            c.css("top", (300 + $(window).scrollTop()) + "px")
        }
    });
    d.click(function() {
        $("body,html").scrollTop(0);
        return false
    })
}
function getProvinceName() {
    var b = jQuery.cookie("provinceId");
    if (!b) {
        b = 1
    }
    return YHDPROVINCE.proviceObj["p_" + b]
}
YHD.HomePage.Tools = YHD.HomePage.Tools || {};
YHD.HomePage.Tools.hoverEvent = function(u) {
    var v = u.box;
    var m = u.tab;
    var o = u.tabContent;
    var p = u.callback;
    var t = u.isTracker;
    var r = u.hoverCallback;
    var s = $(v), n = s.find(m), q = s.find(o);
    q.hide().eq(0).show();
    n.eq(0).data("flag", 1);
    n.hover(function() {
        var b = $(this);
        n.removeClass("cur");
        b.addClass("cur");
        var a = n.index(this);
        q.hide().eq(a).show();
        if (t == 1) {
            var d = b.data("flag");
            if (d != "1") {
                var c = b.attr("tk");
                gotracker("2", c);
                b.data("flag", "1")
            }
        }
        if (r) {
            r(this)
        }
    });
    if (p) {
        p(s)
    }
};
YHD.HomePage.Tools.needReadPms = function() {
    var b = jQuery.cookie("abtest");
    if (b >= 1 && b <= 25) {
        return true
    }
    return false
};
YHD.HomePage.Tools.getNowTime = function() {
    var d;
    if (typeof (nowTime) == "undefined" || nowTime == undefined) {
        var c = new Date();
        d = new Array(c.getFullYear(), c.getMonth() + 1, c.getDate(), c.getHours(), c.getMinutes(), c.getSeconds())
    } else {
        d = nowTime.split("-")
    }
    return new Date(d[0], d[1] - 1, d[2], d[3], d[4], d[5])
};
YHD.HomePage.Tools.calLimitTime = function(d) {
    var e = YHD.HomePage.Tools.getNowTime();
    var f = e.getTime();
    $(d).each(function() {
        var a = $(this);
        var l = a.find(".endTime").val();
        if (l != "0") {
            var k = l.split("-");
            if (k.length == 6) {
                var c = new Date(k[0], k[1] - 1, k[2], k[3], k[4], k[5]);
                var b = c.getTime();
                a.find(".limitBuyRemainTime").val(b - f)
            }
        }
    })
};
YHD.HomePage.Tools.countdownTime = function(d, e) {
    var f = [];
    $(d).each(function() {
        var a = $(this);
        f.push({el: a.find(".last_time"),time: a.find(".limitBuyRemainTime").val()})
    });
    setInterval(function() {
        for (i = 0, j = f.length; i < j; i++) {
            var r = f[i], b = r.time, c = r.el;
            var o = b / 1000;
            if (o >= 0) {
                var n = Math.floor(o % 60);
                var a = Math.floor((o / 60) % 60);
                var q = Math.floor((o / 3600) % 24);
                var p = Math.floor((o / 3600) / 24);
                if (a >= 0 && a <= 9) {
                    a = "0" + a
                }
                if (n >= 0 && n <= 9) {
                    n = "0" + n
                }
                q = p * 24 + q;
                if (e) {
                    msg = e(q, a, n)
                }
                if (q == 0 && a == 0 && n == 0) {
                } else {
                    $(c).html(msg)
                }
            }
            f[i].time -= 1000
        }
    }, 1000)
};
YHD.HomePage.Tools.rate = function(x, z) {
    var t = z, s = t.style;
    var o = !-[1, ];
    if (o) {
        var B = x * Math.PI / 180, q = Math.cos(B), r = -Math.sin(B), y = Math.sin(B), A = Math.cos(B);
        t.fw = t.fw || t.offsetWidth / 2;
        t.fh = t.fh || t.offsetHeight / 2;
        var w = (90 - x % 90) * Math.PI / 180, u = Math.sin(w) + Math.cos(w);
        s.filter = "progid:DXImageTransform.Microsoft.Matrix(M11=" + q + ",M12=" + r + ",M21=" + y + ",M22=" + A + ",SizingMethod='auto expand')";
        s.top = t.fh * (1 - u) + "px";
        s.left = t.fw * (1 - u) + "px"
    } else {
        var v = "rotate(" + x + "deg)";
        s.MozTransform = v;
        s.WebkitTransform = v;
        s.OTransform = v;
        s.msTransform = v;
        s.Transform = v
    }
    return false
};
YHD.HomePage.initLunbo = function() {
    var p = $("#lunbo_1");
    var n = p.closest("li").find(".mini_promo img");
    s(n);
    q(p.closest("li"));
    var m = $("#lunbo_2");
    if (m.length > 0) {
        o(m, function() {
            if (!m.data("data-loaded")) {
                $("#lunboNum").show();
                r("#promo_show");
                m.data("data-loaded", 1)
            }
        });
        setTimeout(function() {
            if (!m.data("data-loaded")) {
                $("#lunboNum").show();
                r("#promo_show");
                m.data("data-loaded", 1)
            }
        }, 3000)
    }
    l();
    function r(b) {
        a();
        function a() {
            var K = $("ol", "#promo_show").width(), P = $("#promo_show>ul>li"), d = $("#index_menu_carousel>ol"), T = $("#index_menu_carousel>ol>li"), J = T.length, M, h = false, I;
            var f = T.first();
            T.last().clone().prependTo(d);
            d.width(K * (J + 2) + 100).css("left", "-" + K + "px");
            $("#promo_show>a").attr("hidefocus", "true").css("outline", "none");
            $("#promo_show>a").eq(1).css({left: -45,opacity: 0,right: "auto"}).prev().css({right: -45,opacity: 0,left: "auto"});
            $("#promo_show").css("overflow", "hidden");
            $("#promo_show").hover(function() {
                $(this).children("a").show();
                $(this).children("a").eq(1).stop().animate({left: 0,opacity: 1}).prev().stop().animate({right: 0,opacity: 1});
                clearInterval(M);
                e();
                h = true;
                return false
            }, function() {
                $(this).children("a").eq(1).stop().animate({left: -45,opacity: 0}, function() {
                    $(this).hide()
                }).prev().stop().animate({right: -45,opacity: 0}, function() {
                    $(this).hide()
                });
                clearInterval(M);
                M = setInterval(function() {
                    H(c(), true)
                }, 5000);
                S();
                h = false;
                return false
            });
            M = setInterval(function() {
                H(c(), true)
            }, 5000);
            L();
            P.hover(function() {
                var u = this;
                var v = P.index(u);
                I = setTimeout(function() {
                    $(u).addClass("cur").siblings().removeClass("cur");
                    $("ol", "#index_menu_carousel").stop(true).animate({left: "-" + (v + 1) * K + "px"}, 360);
                    N();
                    R(c());
                    q($(T.eq(c())))
                }, 100)
            }, function() {
                if (I) {
                    clearTimeout(I)
                }
            });
            $(".show_next,.show_pre", "#promo_show").click(function() {
                var u = c();
                if ($("ol", "#index_menu_carousel").is(":animated")) {
                    return
                }
                if ($(this).hasClass("show_pre")) {
                    $("ol", "#index_menu_carousel").animate({left: "+=" + K + "px"}, 360, function() {
                        if (u > 0) {
                            P.eq(u - 1).addClass("cur").siblings().removeClass("cur");
                            R(u - 1);
                            q($(T.eq(u - 1)))
                        } else {
                            if (u == 0) {
                                $("ol", "#index_menu_carousel").css("left", "-" + K * (J) + "px");
                                P.eq(-1).addClass("cur").siblings().removeClass("cur");
                                var v = $("#index_menu_carousel>ol>li").eq(0).find(".mini_promo img");
                                s(v);
                                R(J - 1);
                                q($(T.eq(J - 1)))
                            }
                        }
                        N()
                    })
                } else {
                    H(u)
                }
                return false
            });
            function H(v, u) {
                if (v == J - 1) {
                    f.addClass("cur").css("left", K * J)
                }
                $("ol", "#index_menu_carousel").stop(true, true).animate({left: "-=" + K + "px"}, 360, function() {
                    if (v < J - 1) {
                        P.eq(v + 1).addClass("cur").siblings().removeClass("cur");
                        R(v + 1);
                        q($(T.eq(v + 1)))
                    } else {
                        if (v == J - 1) {
                            f.removeClass("cur").css("left", -K);
                            $("ol", "#index_menu_carousel").css("left", "-" + K + "px");
                            P.eq(0).addClass("cur").siblings().removeClass("cur")
                        }
                    }
                    if (u && !h) {
                        L()
                    } else {
                        N()
                    }
                })
            }
            function c() {
                return $("ul>li", "#promo_show").index($("ul>li.cur", "#promo_show"))
            }
            function R(w) {
                var v = $(T.eq(w));
                var x = v.data("data-loader");
                if (!x && x != 1) {
                    var u = v.find(".mini_promo img");
                    s(u);
                    v.data("data-loaded", 1)
                }
            }
            function S() {
                var u = $("#promo_show>ul>li.cur");
                var v = u.find("span").width();
                u.siblings().find("span").css("width", 0);
                if (v != u.width()) {
                    u.find("span").animate({width: "100%"}, 4640, function() {
                        $(this).width(0)
                    })
                }
            }
            function e() {
                var u = $("#promo_show>ul>li.cur");
                u.siblings().find("span").stop().css("width", 0);
                u.find("span").stop()
            }
            function N() {
                var u = $("#promo_show>ul>li.cur");
                u.siblings().find("span").stop().css("width", 0);
                u.find("span").stop().width("100%")
            }
            function L() {
                var u = $("#promo_show>ul>li.cur");
                u.siblings().find("span").css("width", 0);
                u.find("span").width(0).animate({width: "100%"}, 4640, function() {
                    $(this).width(0)
                })
            }
            var O = t();
            var k = $("#promo_show").find(".big_pic img[" + O + "]");
            len = k.length, flag = 0;
            var Q = setInterval(function() {
                if (flag >= len) {
                    clearInterval(Q);
                    return
                }
                var v = $(k[flag]);
                var u = v.attr(O);
                if (u) {
                    v.attr("src", u);
                    v.removeAttr(O)
                }
                flag++
            }, 200);
            function g() {
                var x = 0, u = 0;
                function y(D) {
                    try {
                        D.preventDefault();
                        var B = D.touches[0];
                        var E = Number(B.pageX);
                        x = E;
                        clearInterval(M);
                        e();
                        h = true
                    } catch (C) {
                    }
                }
                function v(D) {
                    try {
                        D.preventDefault();
                        var B = D.touches[0];
                        var E = Number(B.pageX);
                        u = E - x
                    } catch (C) {
                    }
                }
                function z(C) {
                    try {
                        C.preventDefault();
                        if (u < -20) {
                            $(".show_next", "#promo_show").click()
                        }
                        if (u > 20) {
                            $(".show_pre", "#promo_show").click()
                        }
                        u = 0;
                        clearInterval(M);
                        M = setInterval(function() {
                            H(c(), true)
                        }, 5000);
                        S();
                        h = false
                    } catch (B) {
                    }
                }
                function A() {
                    var B = document.getElementById("index_menu_carousel");
                    if (window.addEventListener) {
                        B.addEventListener("touchstart", y, false);
                        B.addEventListener("touchmove", v, false);
                        B.addEventListener("touchend", z, false)
                    } else {
                        if (window.attachEvent) {
                            B.attachEvent("touchend", z);
                            B.attachEvent("touchmove", v);
                            B.attachEvent("touchend", z)
                        }
                    }
                }
                function w() {
                    try {
                        document.createEvent("TouchEvent");
                        A()
                    } catch (B) {
                    }
                }
                w()
            }
            g()
        }
    }
    function s(c) {
        var b = c.length;
        for (var e = 0; e < b; e++) {
            var a = $(c[e]);
            var d = a.attr(t());
            if (d) {
                a.attr("src", d);
                a.removeAttr(t())
            }
        }
    }
    function o(a, c) {
        a = $(a);
        var b = t();
        var d = a.attr(b);
        if (d) {
            a.load(function() {
                var e = a.data("data-callback");
                if (c && !e) {
                    c.call(this);
                    a.data("data-callback", 1)
                }
            });
            a.attr("src", d);
            a.removeAttr(b)
        }
    }
    function t() {
        var a = "si";
        if (window.isWidescreen) {
            a = "wi"
        }
        return a
    }
    function q(d) {
        var h = jQuery.cookie("provinceId");
        if (!h) {
            h = 0
        }
        var a = d.data("data-trackerFlag");
        if (a != 1 && h != 0) {
            d.data("data-trackerFlag", 1);
            var k = d.find(".big_pic").attr("data-ref");
            recordTrackInfoWithType("1", k + "_" + h + "_" + 0, "ad.dolphin.cpt");
            var g = d.find(".mini_promo a");
            for (var c = 0, b = g.length; c < b; c++) {
                var e = jQuery(g[c]);
                var f = e.attr("data-ref");
                recordTrackInfoWithType("1", f + "_" + h + "_" + 0, "ad.dolphin.cpt")
            }
        }
    }
    function l() {
        var a = a || {};
        a.u_hover = function(b) {
            var c = $(b);
            c.hover(function() {
                $(this).removeClass("hovers").siblings().addClass("hovers")
            }, function() {
                $(this).siblings().removeClass("hovers")
            })
        };
        a.initFun = function() {
            a.u_hover("#index_menu_carousel .mini_promo a")
        };
        a.initFun()
    }
};
YHD.HomePage.initPromotTab = function() {
    var param = {box: "#index_recommend_list",tab: ".pro_tab li",tabContent: ".tab_content",callback: function(dom) {
            dom.delegate("li", "mouseover", function() {
                $(this).removeClass("li_hover");
                $(this).addClass("li_hover")
            });
            dom.delegate("li", "mouseout", function() {
                $(this).removeClass("li_hover")
            });
            $("#index_recommend_list ul.pro_tab").find("li:last").addClass("last_tab")
        },isTracker: 1,hoverCallback: function(dom) {
            var id = $(dom).attr("data-callback");
            if (id) {
                initHoverCallback(id)
            }
        }};
    YHD.HomePage.Tools.hoverEvent(param);
    function initHoverCallback(id) {
        if (id == "youFavorateProduct") {
            initYouFavorateProduct("#youFavorateProduct")
        } else {
            if (id == "tryProductRecommend") {
                initTryProductRecommend("#tryProductRecommend")
            } else {
                if (id == "limitBuy") {
                    initLimitBuyAdaptor("#limitBuy")
                } else {
                    if (id.indexOf("meitehaoProductRecommend") == 0) {
                        getAjaxProductPrice("#" + id)
                    }
                }
            }
        }
    }
    function initLimitBuyAdaptor(sid) {
        var obj = $(sid);
        if (obj.data("isLoad")) {
            return
        }
        obj.data("isLoad", 1);
        getServerTimeAndCalLimitTime()
    }
    function initYouFavoratePromotion(sid) {
        initTabData(sid, URLPrefix.pms + "/homePage/getFavoratePromotion.do", "pms_localfavor_")
    }
    function initYouFavorateProduct(sid) {
        initTabData(sid, URLPrefix.pms + "/homePage/guessYourFavorateProducts.do", "pms_guessyourfavor_")
    }
    function initTryProductRecommend(sid) {
        var url = "http://interface.yhd.com/trial/general/ajaxGetTrialProdForHomePage.do";
        var trackerPrefix = "try_homepage_";
        var obj = $(sid);
        var provinceId = jQuery.cookie("provinceId");
        var siteId = currSiteId || 1;
        var siteType = currSiteType || 1;
        var widescreen = isWidescreen ? 1 : 0;
        if (obj.data("data-isLoad")) {
            return
        }
        if (!provinceId) {
            return
        }
        var param = {provinceId: provinceId,siteId: siteId,siteType: siteType,widescreen: widescreen};
        obj.data("data-isLoad", 1);
        $.ajax({url: url,type: "GET",dataType: "jsonp",jsonpCallback: "jsonpcallbackForTry",data: param,success: function(result) {
                var rs = result;
                if (rs) {
                    if (rs.code == "0" && rs.data != null && rs.data.length > 0) {
                        var total = rs.data.length;
                        var pageSize = param.widescreen == 1 ? 5 : 4;
                        var pageCount = (total % pageSize == 0) ? Math.floor(total / pageSize) : Math.floor(total / pageSize) + 1;
                        obj.data("curPage", 1);
                        obj.data("pageSize", pageSize);
                        obj.data("total", total);
                        obj.data("pageCount", pageCount);
                        obj.data("data", rs.data);
                        _appendHtml(rs.data);
                        _registerEvent()
                    } else {
                        _appendHtml(null)
                    }
                } else {
                    _appendHtml(null)
                }
            }});
        var _appendHtml = function(data) {
            var html = [];
            if (data == null) {
                html.push('<li class="none_list"><span class="none_product">很抱歉，您查看的商品不存在或者已下架</span></li>');
                obj.removeClass("global_loading");
                obj.find("ul").html(html.join(""))
            } else {
                var curPage = obj.data("curPage");
                var total = obj.data("total");
                var pageSize = obj.data("pageSize");
                var pageCount = obj.data("pageCount");
                var start = (curPage - 1) * pageSize;
                var end = (start + pageSize) < total ? (start + pageSize) : total;
                for (var i = start; i < end; i++) {
                    var price = data[i].nonMemberPrice;
                    var tracker = data[i].tk;
                    var url = data[i].linkUrl;
                    var isAd = data[i].isAd;
                    html.push("<li>");
                    html.push("<div class='li_box'>");
                    html.push("<a href='" + url + "' target='_blank' title='" + data[i].productCname + "' class='pro_img'  data-ref='" + tracker + "'><img src='" + data[i].picUrl160x160 + "' />" + (isAd != null ? "<sup class='pinpai_label'>品牌试用</sup>" : "") + "</a>");
                    html.push("<a href='" + url + "' target='_blank' title='" + data[i].productCname + "' class='pro_name' data-ref='" + tracker + "'>" + data[i].productCname + "</a>");
                    html.push("<p class='tl' productId='" + data[i].productId + "'><span>&yen;</span><strong>0</strong><del>&yen;" + price + "</del></p>");
                    html.push("</div>");
                    html.push("</li>")
                }
                obj.removeClass("global_loading");
                obj.find("ul").html(html.join(""));
                var alreadySendTkIds = obj.data("alreadySendTkIds") != null ? obj.data("alreadySendTkIds").split(",") : [];
                for (var i = start; i < end; i++) {
                    if (data[i].isAd == "1" && data[i].adType == "cpt") {
                        var productId = data[i].productId;
                        var alreadySend = false;
                        for (var j = 0; j < alreadySendTkIds.length; j++) {
                            if (alreadySendTkIds[j] == productId) {
                                alreadySend = true;
                                break
                            }
                        }
                        if (!alreadySend) {
                            recordTrackInfoWithType("1", data[i].tk, "ad.dolphin.cpt");
                            alreadySendTkIds.push(productId);
                            obj.data("alreadySendTkIds", alreadySendTkIds.join(","))
                        }
                    }
                }
            }
        };
        var _registerEvent = function() {
            var pre = $(".tabpre", obj);
            var next = $(".tabnext", obj);
            pre.click(function() {
                var data = obj.data("data");
                var curPage = obj.data("curPage");
                var pageCount = obj.data("pageCount");
                if (!data) {
                    return
                }
                if (curPage > 1) {
                    obj.data("curPage", curPage - 1)
                } else {
                    obj.data("curPage", pageCount)
                }
                _appendHtml(data);
                gotracker("2", trackerPrefix + "leftbutton")
            });
            next.click(function() {
                var data = obj.data("data");
                var curPage = obj.data("curPage");
                var pageCount = obj.data("pageCount");
                if (!data) {
                    return
                }
                if (curPage < pageCount) {
                    obj.data("curPage", curPage + 1)
                } else {
                    obj.data("curPage", 1)
                }
                _appendHtml(data);
                gotracker("2", trackerPrefix + "rightbutton")
            })
        }
    }
    function initTabData(sid, url, trackerInfo) {
        if (!jQuery.cookie("provinceId")) {
            return
        }
        if (jQuery(sid).data("data-isLoad")) {
            return
        }
        jQuery(sid).data("data-isLoad", true);
        var provinceId = jQuery.cookie("provinceId");
        var siteId = currSiteId || 1;
        var siteType = currSiteType || 1;
        var widescreen = isWidescreen ? 1 : 0;
        var url = url + "?provinceId=" + provinceId + "&currSiteId=" + siteId + "&currSiteType=" + siteType + "&widescreen=" + widescreen;
        pageScroll(sid, url, trackerInfo)
    }
    function pageScroll(boxid, url, trackerInfo) {
        var id = boxid, listNum = screen.width >= 1200 ? 5 : 4, pageTotal = 1, forbidClick = true;
        var isLoad = "data-isLoaded";
        var list = "data-list";
        var length = "data-length";
        loadData(id, url);
        function loadData(boxid, url) {
            if ($(boxid).data(isLoad)) {
                return
            }
            $.ajax({url: url,dataType: "jsonp",success: function(data) {
                    if (data == null || data.value == null || data.value.length < 1) {
                        showHtml(boxid, trackerInfo, null);
                        return
                    }
                    var obj = data.value[0];
                    var dataList = obj.data;
                    var tp = obj.trackerPrefix;
                    if (tp && tp != "") {
                        trackerInfo = tp
                    }
                    $(boxid).data(isLoad, true);
                    $(boxid).data(list, dataList);
                    pageTotal = parseInt((obj.length - 1) / listNum + 1);
                    showData(boxid, 1, trackerInfo);
                    bindClick()
                }})
        }
        function bindClick() {
            $(".prev,.next", id).click(function() {
                if (!forbidClick) {
                    return false
                }
                var currentPage = $(id).data("page");
                if (!currentPage) {
                    return
                }
                forbidClick = false;
                if ($(this).hasClass("prev")) {
                    var n = currentPage - 1;
                    if (n <= 0) {
                        n = pageTotal
                    }
                    showData(boxid, n, trackerInfo);
                    gotracker("2", trackerInfo + "leftbutton")
                } else {
                    var n = currentPage + 1;
                    if (n > pageTotal) {
                        n = 1
                    }
                    showData(boxid, n, trackerInfo);
                    gotracker("2", trackerInfo + "rightbutton")
                }
                return false
            })
        }
        function showData(boxid, pageNum, trackerInfo) {
            var data = $(boxid).data(list);
            if (!data) {
                return
            }
            if (pageNum == pageTotal) {
                data = data.slice((pageNum - 1) * listNum)
            } else {
                data = data.slice((pageNum - 1) * listNum, pageNum * listNum)
            }
            $(boxid).data("page", pageNum);
            showHtml(boxid, trackerInfo, data);
            forbidClick = true
        }
        function showHtml(boxid, trackerInfo, data) {
            var html = genHtmlWithPms(data, boxid, trackerInfo);
            $(boxid).removeClass("global_loading");
            $(boxid).find("ul").html(html);
            getAjaxProductPrice(boxid)
        }
    }
    function genHtmlWithPms(data, sid, trackerInfo) {
        if (!jQuery.cookie("provinceId")) {
            return
        }
        var provinceId = jQuery.cookie("provinceId");
        if (data == null || data == "" || data.length < 1) {
            return '<li class="none_list"><span class="none_product">很抱歉，您查看的商品不存在或者已下架</span></li>'
        }
        if (typeof youFavorateICO == "undefined" || youFavorateICO == "") {
            youFavorateICO = {}
        }
        var pmsHtml = [];
        var db = data;
        for (var i = 0, j = db.length; i < j; i++) {
            if (i >= 5) {
                break
            }
            var dd = db[i];
            pmsHtml.push("<li>");
            pmsHtml.push('<div class="li_box">');
            var track = trackerInfo + provinceId + "_" + dd.productId + "_" + merchant;
            pmsHtml.push('<a href="' + dd.linkUrl + '" class="pro_img" data-ref="' + track + '" title="' + dd.cnName + '" target="_blank">');
            pmsHtml.push('<img src="' + dd.picUrl + '"/>');
            pmsHtml.push("</a>");
            pmsHtml.push('<a class="pro_name" href="' + dd.linkUrl + '" data-ref="' + track + '" title="' + dd.cnName + '" target="_blank">' + dd.cnName + "</a>");
            pmsHtml.push('<p class="tl" productId="' + dd.productId + '"><span>¥</span><strong>' + dd.salePrice + "</strong>");
            if (globalShowMarketPrice == 1) {
                pmsHtml.push("<del>¥" + dd.marketPrice + "</del></p>")
            } else {
                if (dd.yhdPrice && dd.yhdPrice != 0 && dd.yhdPrice > dd.salePrice) {
                    pmsHtml.push("<del>¥" + dd.yhdPrice + "</del></p>")
                }
            }
            eval("var youFavorateICOJson = " + youFavorateICO);
            var icoUrl = "";
            if (dd.recommendType) {
                icoUrl = youFavorateICOJson[dd.recommendType]
            }
            var icoImg = (typeof icoUrl == "undefined" || icoUrl == "") ? "<img src='" + URLPrefix.statics + "/global/images/loveico.jpg' />" : "<img src='" + icoUrl + "'/>";
            if (sid && sid == "#youFavorateProduct") {
                if (dd.recommend) {
                    if (dd.categoryId && dd.categoryName) {
                        var categoryUrl = URLPrefix.search + "/s2/c" + dd.categoryId + "-" + dd.categoryName + "/";
                        var recommend = dd.recommend;
                        var categoryName = dd.categoryName.length > 5 ? dd.categoryName.substring(0, 4) + "..." : dd.categoryName;
                        recommend = recommend.replace("<categoryName>", '<a title="' + dd.categoryName + '" class="blue_link" href="' + categoryUrl + '" data-ref="pms_homepage_category_' + dd.categoryId + "_" + i + '" target="_blank">' + categoryName + "</a>");
                        pmsHtml.push('<p class="recommend">' + recommend + "</p>")
                    } else {
                        pmsHtml.push('<p class="recommend">' + dd.recommend + "</p>")
                    }
                }
            }
            pmsHtml.push("</div>");
            pmsHtml.push("</li>")
        }
        return pmsHtml.join("")
    }
    function initLimitBuy() {
        var obj = $.parseJSON($("body").attr("data-param"));
        var tierthirdProvinceFlag = obj != null ? obj.tierthirdProvinceFlag : "0";
        if ((typeof tierthirdProvinceFlag != "undefined" && tierthirdProvinceFlag == "1") || !YHD.HomePage.Tools.needReadPms() || limitBuyCallFlag == "0") {
            showDefaultData();
            return
        }
        if (!jQuery.cookie("provinceId")) {
            return
        }
        var dom = jQuery("#limitBuy");
        if (dom.data("data-isLoad")) {
            return
        }
        dom.data("data-isLoad", true);
        var provinceId = jQuery.cookie("provinceId");
        var siteId = currSiteId || 1;
        var siteType = 1;
        var widescreen = isWidescreen ? 1 : 0;
        var url = URLPrefix.pms + "/homePage/homePageLimitBuy.do";
        url = url + "?provinceId=" + provinceId + "&currSiteId=" + siteId + "&currSiteType=" + siteType + "&widescreen=" + widescreen;
        var isLoadFinish = false;
        try {
            jQuery.ajax({url: url,dataType: "jsonp",timeout: 2000,success: function(data) {
                    if (isLoadFinish) {
                        return
                    }
                    data = data.value;
                    if (data == null || data.length < 1 || data[0].data == null || data[0].data.length < 1) {
                        showDefaultData();
                        return
                    }
                    data = data[0];
                    var trackerPrefix = data.trakerPrefix;
                    var result = [];
                    for (var i = 0, len = data.data.length; i < len; i++) {
                        var obj = data.data[i];
                        var promEndDate = obj.promEndDate;
                        var now = YHD.HomePage.Tools.getNowTime().getTime();
                        if (jQuery.trim(obj.promEndDate) == "" || now > getDate(promEndDate).getTime()) {
                            continue
                        }
                        var tk = trackerPrefix + obj.productId;
                        result.push("<li>");
                        result.push('<div class="li_box">');
                        result.push('<a href="' + obj.linkUrl + '" data-ref="' + tk + '"   class="pro_img"  title="' + obj.cnName + '" target="_blank">');
                        result.push('<img src="' + obj.picUrl + '"/></a>');
                        result.push('<a href="' + obj.linkUrl + '" data-ref="' + tk + '"   class="pro_name"  title="' + obj.cnName + '" target="_blank">');
                        result.push(obj.cnName);
                        result.push("</a>");
                        result.push('<p class="tl" productId="' + obj.productId + '">');
                        result.push("<span>¥</span><strong>" + obj.salePrice + "</strong>");
                        result.push("<del>¥" + obj.yhdPrice + "</del>");
                        result.push("</p>");
                        result.push('<p class="last_time"></p>');
                        result.push('<input type="hidden" class="limitBuyRemainTime" value="' + 0 + '" />');
                        result.push('<input type="hidden" class="endTime" value="' + tranDateFormat(obj.promEndDate) + '" />');
                        result.push("</div>");
                        result.push("</li>")
                    }
                    dom.html(result.join(""));
                    callback();
                    isLoadFinish = true
                },error: function() {
                    showDefaultData()
                }})
        } catch (e) {
            showDefaultData()
        }
        setTimeout(function() {
            if (!isLoadFinish) {
                showDefaultData()
            }
        }, 2000);
        function showDefaultData() {
            isLoadFinish = true;
            var limitBuy = jQuery("#limitBuy");
            limitBuy.html(jQuery("#limitBuy_textarea").val());
            var imgs = limitBuy.find("img");
            jQuery.each(imgs, function(i, n) {
                var dom = jQuery(n);
                if (dom.attr("original")) {
                    dom.attr("src", dom.attr("original"));
                    dom.removeAttr("original")
                }
            });
            callback()
        }
        function callback() {
            YHD.HomePage.Tools.calLimitTime("#limitBuy li");
            YHD.HomePage.Tools.countdownTime("#limitBuy li", function(hour, minute, second) {
                return "剩余<span>" + hour + "小时" + minute + "分" + second + "秒</span>"
            });
            getAjaxProductPrice("#limitBuy");
            jQuery("#limitBuy").removeClass("global_loading")
        }
        function getDate(strDate) {
            var st = strDate;
            var a = st.split(" ");
            var b = a[0].split("-");
            var c = a[1].split(":");
            var date = new Date(b[0], b[1] - 1, b[2], c[0], c[1], c[2]);
            return date
        }
        function tranDateFormat(d) {
            if (jQuery.trim(d).length < 1) {
                return "0"
            }
            try {
                var a = d.split(" ");
                var b = a[0].split("-");
                var c = a[1].split(":");
                return b[0] + "-" + b[1] + "-" + b[2] + "-" + c[0] + "-" + c[1] + "-" + c[2]
            } catch (e) {
                return "0"
            }
        }
    }
    function getServerTimeAndCalLimitTime() {
        var url = currDomain + "/time/dynamictime";
        $.ajax({url: url,dataType: "script",complete: function(XMLHttpRequest, textStatus) {
                initLimitBuy()
            }})
    }
    if (32 == $.cookie("provinceId")) {
        getAjaxProductPrice("#meitehaoProductRecommend0");
        return
    }
    var isFirstLimitBuy = 1;
    var firstTab = $("#index_recommend_list ul.pro_tab").find("li:first");
    if (firstTab.attr("data-callback") != "limitBuy" && firstTab.attr("data-callback") != null) {
        isFirstLimitBuy = 0
    }
    if (isFirstLimitBuy) {
        initLimitBuyAdaptor("#limitBuy")
    } else {
        initYouFavorateProduct("#youFavorateProduct")
    }
};
YHD.HomePage.initFloor = function() {
    var f = jQuery("#needLazyLoad").find(".mod_index_floor"), e;
    f.delegate(".mid_img_list a", "mouseenter", function() {
        jQuery(this).addClass("cur").find(".hover_show").show()
    });
    f.delegate(".mid_img_list a", "mouseleave", function() {
        jQuery(this).removeClass("cur").find(".hover_show").hide()
    });
    f.delegate(".right_tab li", "mouseenter", function() {
        var a = jQuery(this), b;
        e = setTimeout(function() {
            b = a.index();
            a.addClass("cur").siblings("li").removeClass("cur");
            a.parents(".right_module").find(".tab_content_wrap").hide().eq(b).show();
            var c = a.parents(".right_module").find(".right_tab_content .tab_content_wrap").eq(b).find("img");
            jQuery.each(c, function(n, l) {
                var m = jQuery(l);
                if (m.attr("original")) {
                    m.attr("src", m.attr("original"));
                    m.removeAttr("original")
                }
            })
        }, 100)
    });
    f.delegate(".right_tab li", "mouseleave", function() {
        if (e) {
            clearTimeout(e)
        }
    });
    f.delegate(".right_tab_content li", "mouseenter", function() {
        jQuery(this).addClass("cur").siblings("li").removeClass("cur")
    });
    f.delegate(".right_tab_content li", "mouseleave", function() {
        jQuery(this).removeClass("cur")
    });
    f.delegate(".brand_banner", "mouseenter", function() {
        jQuery(this).addClass("cur_shadow");
        var a = $(".floor8");
        if (a.data("isLoad")) {
            return
        }
        a.data("isLoad", 1);
        var b = currDomain + "/time/dynamictime";
        $.ajax({url: b,dataType: "script",complete: function(h, c) {
                YHD.HomePage.Tools.calLimitTime(".brand_banner.need_count_down");
                YHD.HomePage.Tools.countdownTime(".brand_banner.need_count_down", function(n, g, m) {
                    return "<i></i>剩余<span><em>" + n + "</em>小时<em>" + g + "</em>分<em>" + m + "</em>秒</span>"
                })
            }})
    });
    f.delegate(".brand_banner", "mouseleave", function() {
        jQuery(this).removeClass("cur_shadow")
    });
    var d = !-[1, ];
    f.delegate(".brand_logo_white", "mouseenter", function() {
        jQuery(this).parents(".logo_wrap").find(".discount").show()
    });
    if (!d) {
        f.delegate(".brand_logo_gray", "mouseenter", function() {
            jQuery(this).parents(".logo_wrap").find(".discount").show()
        })
    }
    f.delegate(".brand_logo_wrap .discount", "mouseleave", function() {
        jQuery(".discount").hide()
    })
};
YHD.HomePage.reflushGrouponData = function() {
    var h = jQuery("#currentGroupon");
    if (h.length < 1) {
        return
    }
    var l = h.attr("data-grouponId");
    if (!l || l == -1) {
        return
    }
    var k = jQuery.cookie("provinceId");
    if (!k) {
        return
    }
    var g = URLPrefix.busystock ? URLPrefix.busystock : "http://gps.yihaodian.com";
    var f = g + "/restful/groupon?provinceId=" + k + "&grouponIds=" + l + "&callback=?";
    jQuery.getJSON(f, function(b) {
        if (b == null || b == "" || b.length < 1) {
            return
        }
        var a = b[0];
        if (a.code == 1) {
            h.find("span.num > strong").html(a.soldNum);
            h.find("strong.price > i").html(a.currentPrice)
        }
    })
};
YHD.HomePage.initTuanTab = function() {
    var b = {box: "#index_tuan",tab: ".tabs a",tabContent: ".content",callback: null,isTracker: 1,hoverCallback: null};
    YHD.HomePage.Tools.hoverEvent(b)
};
YHD.HomePage.initNewsTab = function() {
    var e = jQuery.cookie("provinceId");
    var d = function() {
        var a = $("#index_news").find(".tb1");
        if (a != null) {
            var b = a.attr("tk");
            recordTrackInfoWithType("1", b + "_" + e + "_" + 0, "ad.dolphin.cpt")
        }
    };
    var f = {box: "#index_news",tab: ".tabs a",tabContent: ".content",callback: d,isTracker: 1,hoverCallback: null};
    YHD.HomePage.Tools.hoverEvent(f)
};
YHD.HomePage.initIE6UpdateMsg = function() {
    var k = window.navigator.userAgent.toLowerCase();
    var h = /msie ([\d.]+)/;
    if (h.test(k)) {
        var g = parseInt(h.exec(k)[1]);
        var l = $.cookie("ie6Update");
        if (g <= 6 && "1" != l) {
            var f = [];
            f.push("<div class='ie6_upgrade clearfix' id='ie6_upgrade'>");
            f.push("<div class='ie6_upgrade_wrap'>");
            f.push("<span class='ie6_upgrade_sad'></span>");
            f.push("<span class='ie6_upgrade_text'>温馨提示：您当前使用的浏览器版本过低，兼容性和安全性较差，1号店建议您升级：</span>");
            f.push("<a href='http://windows.microsoft.com/zh-cn/internet-explorer/download-ie' target='_blank' class='ie6_upgrade_ie' tk='global_ie6_upgrade_ie8'>IE8浏览器</a>");
            f.push("<span class='ie6_upgrade_text'>或</span>");
            f.push("<a href='http://chrome.360.cn/' target='_blank' class='ie6_upgrade_360' tk='global_ie6_upgrade_360'>360极速浏览器</a>");
            f.push("</div>");
            f.push("<a href='javascript:void(0);' class='ie6_upgrade_close' title='关闭' tk='global_ie6_upgrade_close'>关闭提示</a>");
            f.push("</div>");
            $(document.body).prepend(f.join(""));
            $("#ie6_upgrade").show();
            $("#ie6_upgrade a.ie6_upgrade_close").click(function() {
                $("#ie6_upgrade").slideUp();
                $.cookie("ie6Update", "1", {expires: 7,path: "/",domain: no3wUrl});
                var a = $(this).attr("tk");
                gotracker("2", a)
            });
            $("#ie6_upgrade>div>a").click(function() {
                var a = $(this).attr("tk");
                gotracker("2", a)
            })
        }
    }
};
YHD.HomePage.initDigitTab = function() {
    var f = $("#index_digit");
    if (f.size() == 0) {
        return
    }
    var e = $.trim(f.attr("data-url"));
    if (e) {
        f.data("iframeLoaded", "1");
        if (e.indexOf("?") == -1) {
            e = e + "?randid=" + Math.random()
        } else {
            e = e + "&randid=" + Math.random()
        }
        var d = "<iframe src='" + e + "' width='205' height='230' frameborder='0' scrolling='no'></iframe>";
        f.html(d)
    }
};
YHD.HomePage.initUnionSiteAndIE6UpdateMsg = function() {
    if (typeof openUnionSiteFlag == "undefined" || openUnionSiteFlag == "0") {
        YHD.HomePage.initIE6UpdateMsg();
        return
    }
    var c = jQuery.cookie("provinceId");
    if (!c) {
        return
    }
    var d = URLPrefix.central + "/homepage/ajaxUnionSiteInfo.do?callback=?";
    $.getJSON(d, function(a) {
        var q = a;
        var n = true;
        if (q) {
            if (q.status == 0) {
                glaCookieHandler.genGlaCookie({provinceId: c})
            } else {
                if (q.status == 1 && q.unionSiteInfo) {
                    var s = q.unionSiteInfo;
                    if (s.hasUnionSite == 0 || !s.isSupportIpNavigation || s.isSupportIpNavigation == 0) {
                        glaCookieHandler.genGlaCookie({provinceId: s.provinceId,cityId: s.cityId})
                    } else {
                        var b = 1;
                        var r = glaCookieHandler.analysisGla();
                        if (r && !r.willingToUnionSite) {
                            b = 0
                        }
                        var r = {provinceId: s.provinceId,cityId: s.cityId,unionSiteDomain: s.unionSiteDomain,willingToUnionSite: b};
                        glaCookieHandler.genGlaCookie(r);
                        if (b) {
                            location.href = "http://" + s.unionSiteDomain + ".yhd.com"
                        } else {
                            n = false;
                            var t = s.provinceName + s.cityName;
                            var o = s.unionSiteName ? s.unionSiteName : t + "联营店";
                            var p = [];
                            p.push("<div class='ie6_upgrade clearfix' id='ie6_upgrade'>");
                            p.push("<div class='ie6_upgrade_wrap'>");
                            p.push("<span class='ie6_upgrade_sad'></span>");
                            p.push("<span class='ie6_upgrade_text'>根据您的IP，我们猜您是" + t + "的用户，欢迎您来1号店</span>");
                            p.push("<a id='global_goto_union_site' href='http://" + s.unionSiteDomain + ".yhd.com' data-ref='global_goto_union_site' style='padding-left:0px;'>" + o + "</a>");
                            p.push("<span class='ie6_upgrade_text'>逛逛</span>");
                            p.push("</div>");
                            p.push("</div>");
                            $(document.body).prepend(p.join(""));
                            $("#ie6_upgrade").show();
                            $("#global_goto_union_site").click(function() {
                                var e = {provinceId: s.provinceId,cityId: s.cityId,unionSiteDomain: s.unionSiteDomain,willingToUnionSite: 1};
                                glaCookieHandler.resetGlaAndProvinceCookie(e)
                            })
                        }
                    }
                }
            }
        }
        if (n) {
            YHD.HomePage.initIE6UpdateMsg()
        }
    })
};
YHD.HomePage.initBaifendian = function() {
    var h = typeof globalBaifendianFlag != "undefined" && globalBaifendianFlag == "0";
    if (h) {
        return
    }
    var e = $.cookie("yihaodian_uid");
    var g = "";
    var f = function(b, a) {
        window._BFD = window._BFD || {};
        _BFD.BFD_INFO = {user_id: b,user_cookie: a,page_type: "homepage"};
        _BFD.client_id = "Cyihaodian";
        _BFD.script = document.createElement("script");
        _BFD.script.type = "text/javascript";
        _BFD.script.async = true;
        _BFD.script.charset = "utf-8";
        _BFD.script.src = (("https:" == document.location.protocol ? "https://ssl-static1" : "http://static1") + ".baifendian.com/service/yihaodian/yihaodian.js");
        document.getElementsByTagName("head")[0].appendChild(_BFD.script)
    };
    setTimeout(function() {
        var a = "http://tracker.yhd.com/pms/getGUID.do?callback=?";
        $.getJSON(a, function(b) {
            if (b.success == "1") {
                g = b.global_user_sign;
                f(e ? e : "", g)
            }
        })
    }, 3 * 1000)
};
YHD.HomePage.initAppreciateAdvertise = function() {
    try {
        var k = typeof globalPmsAdvertiseFlag != "undefined" && globalPmsAdvertiseFlag == "0";
        if (k) {
            return
        }
        var n = $.cookie("yihaodian_uid");
        var l = $("#index_menu_carousel>ol>li[flag=5]");
        if (l.size() == 0) {
            return
        }
        var o = function(c, b) {
            var a = URLPrefix.pms + "/homePage/callUserAppreciateCategoryInfo.do?userid=" + c + "&guid=" + b + "&callback=?";
            $.getJSON(a, function(f) {
                if (f.success == 1) {
                    var g = f.value;
                    var e = "";
                    if (g) {
                        for (var r = 0; r < g.length; r++) {
                            userdata = g[r];
                            if (userdata) {
                                e += "&categoryId=" + userdata.id
                            }
                        }
                        var d = URLPrefix.central + "/homepage/ajaxFindAdvertiseByCategory.do?callback=?" + e;
                        $.getJSON(d, function(I) {
                            if (I.status == 1) {
                                var E = I.advs;
                                var D = [];
                                for (var F = 0; F < E.length; F++) {
                                    var K = E[F];
                                    if (K.bigAdVO.length <= 0 || K.smallAdVO.length <= 0) {
                                        break
                                    }
                                    var q = K.bigAdVO[0];
                                    var H = K.smallAdVO;
                                    D.push("<a href='" + q.imgJumpLinkUrl + "' date-pms='pms' class='big_pic global_loading' target='_blank' data-ref='" + q.perTracker + "'>");
                                    D.push("<img id='lunbo4' alt='" + q.content + "' src='" + URLPrefix.statics + "/global/images/blank.gif' si='" + q.imgPath + "' wi='" + q.imgWidePath + "' />");
                                    D.push("</a>");
                                    D.push("<div class='mini_promo clearfix'>");
                                    for (var G = 0; G < H.length; G++) {
                                        var C = H[G];
                                        D.push("<a class='global_loading' href='" + C.imgJumpLinkUrl + "' target='_blank' data-ref='" + C.perTracker + "'>");
                                        D.push("<img alt='" + C.content + "' src='" + URLPrefix.statics + "/global/images/blank.gif' si='" + C.imgPath + "' wi='" + C.imgWidePath + "' />");
                                        D.push("<u></u>");
                                        D.push("</a>")
                                    }
                                    D.push("</div>")
                                }
                                if (D.length == 0) {
                                    return false
                                }
                                if ($("#lunboNum>li").eq(l.index() - 1).hasClass("cur")) {
                                    return false
                                }
                                l.html(D.join(""));
                                var J = l.find(".big_pic img");
                                p(J);
                                var L = l.closest("li").find(".mini_promo img");
                                p(L)
                            }
                        })
                    }
                }
            })
        };
        function p(e) {
            var d = e.length;
            for (var b = 0; b < d; b++) {
                var c = $(e[b]);
                var a = c.attr(h());
                if (a) {
                    c.attr("src", a);
                    c.removeAttr(h())
                }
            }
        }
        function h() {
            var a = "si";
            if (window.isWidescreen) {
                a = "wi"
            }
            return a
        }
        setTimeout(function() {
            var a = "http://tracker.yhd.com/pms/getGUID.do?callback=?";
            $.getJSON(a, function(b) {
                if (b.success == "1") {
                    userCookie = b.global_user_sign;
                    o(n ? n : "", userCookie)
                }
            })
        }, 3 * 1000)
    } catch (m) {
    }
};
YHD.HomePage.initPreloadAdvertise = function() {
    var A = $("#preloadAdvsData").val();
    var w = (A && A.length > 2) ? $.parseJSON(A) : null;
    var G = function() {
        if ($("#topCurtain").size() > 0 && $("#smallTopBanner img").size() > 1) {
            return 4
        }
        if ($("#topCurtain").size() > 0 && $("#smallTopBanner img").size() == 1) {
            return 3
        }
        if ($("#topbanner").size() > 0 && $("#topbanner img").size() > 1) {
            return 2
        }
        if ($("#topbanner").size() > 0 && $("#topbanner img").size() == 1) {
            return 1
        }
        return 0
    };
    var u = function(b, d) {
        var c = false;
        var a = YHD.HomePage.Tools.getNowTime().getTime();
        if (a >= b && a <= d) {
            c = true
        }
        return c
    };
    var D = function(g, a, c) {
        var e = null;
        var h = g[a];
        var f = h != null ? (h[c] != null ? h[c] : []) : [];
        for (var b = 0; b < f.length; b++) {
            var d = f[b];
            if (u(d.startTime, d.endTime) && d.imgPath && d.imgWidePath) {
                e = d;
                break
            }
        }
        return e
    };
    var C = function(g) {
        if (!g) {
            return {type: 0,data: null}
        }
        var d = D(g, "INDEX_TOP_ZNQSYLAMU_ZHANKAI", 1);
        var a = D(g, "INDEX_TOP_ZNQSYLAMU_SHOUQIZUO", 1);
        var b = D(g, "INDEX_TOP_ZNQSYLAMU_SHOUQIZHONG", 1);
        var c = D(g, "INDEX_TOP_ZNQSYLAMU_SHOUQIYOU", 1);
        var e = D(g, "INDEX_TOP_CURTAINAD_OPEN", 1);
        var l = D(g, "INDEX_TOP_CURTAINAD_CLOSE", 1);
        var f = D(g, "INDEX_TOP_ZNQSYHENGFU_ZUOTU", 1);
        var h = D(g, "INDEX_TOP_ZNQSYHENGFU_ZHONGTU", 1);
        var k = D(g, "INDEX_TOP_ZNQSYHENGFU_YOUTU", 1);
        var m = D(g, "INDEX_TOP_TOPBANNER_DEFAULT", 1);
        if (d != null && a != null && b != null && c != null) {
            return {type: 4,data: {open: d,close1: a,close2: b,close3: c}}
        }
        if (e != null && l != null) {
            return {type: 3,data: {open: e,close: l}}
        }
        if (f != null && h != null && k != null) {
            return {type: 2,data: {adv1: f,adv2: h,adv3: k}}
        }
        if (m != null) {
            return {type: 1,data: {adv: m}}
        }
        return {type: 0,data: null}
    };
    var s = function() {
        var a = $("#smallTopBanner");
        if (a.length < 1) {
            a = $("#topbanner").find(".small_topbanner3")
        }
        if (a.length < 1) {
            return
        }
        a.delegate("a", "mouseover", function() {
            $(this).siblings("a").find("u").show()
        });
        a.delegate("a", "mouseout", function() {
            $(this).siblings("a").find("u").hide()
        })
    };
    var B = function(a, b) {
        a.attr("href", b.imgJumpLinkUrl).attr("title", b.title).attr("data-ref", b.perTracker)
    };
    var z = function(a, b) {
        a.attr("alt", b.title).attr("src", isWidescreen ? b.imgWidePath : b.imgPath);
        if (a.attr("shortimg") != null) {
            a.attr("shortimg", b.imgPath)
        }
        if (a.attr("wideimg") != null) {
            a.attr("wideimg", b.imgWidePath)
        }
        if (a.attr("si") != null) {
            a.attr("si", b.imgPath)
        }
        if (a.attr("wi") != null) {
            a.attr("wi", b.imgWidePath)
        }
    };
    var v = function(a, c) {
        var d = a == 1;
        var b = $("#topbanner");
        var f = c.adv;
        if (d) {
            if (!b.data("preloadFlag")) {
                b.data("preloadFlag", 1);
                B($("#topbanner a"), f);
                z($("#topbanner img"), f)
            }
        } else {
            if (b.size() == 0) {
                var e = [];
                e.push("<div id='topbanner' class='wrap'>");
                e.push("<div class='banner_img'>");
                e.push("<a href='" + f.imgJumpLinkUrl + "' title='" + f.title + "' data-ref='" + f.perTracker + "' target='_blank'>");
                e.push("<img alt='" + f.title + "' src='" + (isWidescreen ? f.imgWidePath : f.imgPath) + "'/>");
                e.push("</a>");
                e.push("</div>");
                e.push("</div>");
                $("#global_top_bar").after(e.join(""));
                $("#topbanner").data("preloadFlag", 1)
            }
        }
    };
    var H = function(g, b) {
        var c = g == 3;
        var a = $("#topCurtain");
        var d = b.open;
        var e = b.close;
        if (c) {
            if (!a.data("preloadFlag")) {
                a.data("preloadFlag", 1);
                B($(".big_topbanner", a), d);
                z($(".big_topbanner img", a), d);
                B($("#smallTopBanner", a), e);
                z($("#smallTopBanner img", a), e)
            }
        } else {
            if (g > 0) {
                $("#topbanner").remove()
            }
            var f = [];
            f.push("<div id='topCurtain' style='display:none;' class='wrap index_topbanner'>");
            f.push("<a class='big_topbanner' href='" + d.imgJumpLinkUrl + "' title='" + d.title + "' data-ref='" + d.perTracker + "' target='_blank'>");
            f.push("<img alt='" + d.title + "' src='" + (URLPrefix.statics + "/global/images/blank.gif") + "' shortimg='" + d.imgPath + "' wideimg='" + d.imgWidePath + "'/>");
            f.push("</a>");
            f.push("<a style='display:none;' id='smallTopBanner' class='small_topbanner' href='" + e.imgJumpLinkUrl + "' title='" + e.title + "' data-ref='" + e.perTracker + "' target='_blank'>");
            f.push("<img alt='" + e.title + "' src='" + (URLPrefix.statics + "/global/images/blank.gif") + "' shortimg='" + e.imgPath + "' wideimg='" + e.imgWidePath + "'/>");
            f.push("</a>");
            f.push("<span title='点击-展开' class='index_topbanner_fold index_topbanner_unfold'>收起<s></s></span>");
            f.push("</div>");
            $("#global_top_bar").after(f.join(""));
            $("#topCurtain").data("preloadFlag", 1)
        }
    };
    var F = function(h, b) {
        var c = h == 2;
        var a = $("#topbanner");
        var e = b.adv1;
        var f = b.adv2;
        var g = b.adv3;
        if (c) {
            if (!a.data("preloadFlag")) {
                a.data("preloadFlag", 1);
                B($("#topbanner a").eq(0), e);
                z($("#topbanner img").eq(0), e);
                B($("#topbanner a").eq(1), f);
                z($("#topbanner img").eq(1), f);
                B($("#topbanner a").eq(2), g);
                z($("#topbanner img").eq(2), g)
            }
        } else {
            if (h > 0) {
                $("#topbanner").remove()
            }
            var d = [];
            d.push("<div id='topbanner' class='wrap'>");
            d.push("<div class='small_topbanner3'>");
            d.push("<a class='small_topbanner3_side' href='" + e.imgJumpLinkUrl + "' title='" + e.title + "' data-ref='" + e.perTracker + "' target='_blank'>");
            d.push("<img alt='" + e.title + "' src='" + (isWidescreen ? e.imgWidePath : e.imgPath) + "'/>");
            d.push("<u style='display: none;'></u>");
            d.push("</a>");
            d.push("<a class='small_topbanner3_m' href='" + f.imgJumpLinkUrl + "' title='" + f.title + "' data-ref='" + f.perTracker + "' target='_blank'>");
            d.push("<img alt='" + f.title + "' src='" + (isWidescreen ? f.imgWidePath : f.imgPath) + "'/>");
            d.push("<u style='display: none;'></u>");
            d.push("</a>");
            d.push("<a class='small_topbanner3_side' href='" + g.imgJumpLinkUrl + "' title='" + g.title + "' data-ref='" + g.perTracker + "' target='_blank'>");
            d.push("<img alt='" + g.title + "' src='" + (isWidescreen ? g.imgWidePath : g.imgPath) + "'/>");
            d.push("<u style='display: none;'></u>");
            d.push("</a>");
            d.push("</div>");
            d.push("</div>");
            $("#global_top_bar").after(d.join(""));
            $("#topbanner").data("preloadFlag", 1)
        }
    };
    var t = function(a, d) {
        var b = a == 4;
        var e = $("#topCurtain");
        var f = d.open;
        var g = d.close1;
        var h = d.close2;
        var k = d.close3;
        if (b) {
            if (!e.data("preloadFlag")) {
                e.data("preloadFlag", 1);
                B($(".big_topbanner", e), f);
                z($(".big_topbanner img", e), f);
                B($("#smallTopBanner a", e).eq(0), g);
                z($("#smallTopBanner img", e).eq(0), g);
                B($("#smallTopBanner a", e).eq(1), h);
                z($("#smallTopBanner img", e).eq(1), h);
                B($("#smallTopBanner a", e).eq(2), k);
                z($("#smallTopBanner img", e).eq(2), k)
            }
        } else {
            if (a > 0) {
                $("#topbanner").remove();
                $("#topCurtain").remove()
            }
            var c = [];
            c.push("<div style='display:none;' id='topCurtain' class='wrap index_topbanner'>");
            c.push("<a class='big_topbanner' href='" + f.imgJumpLinkUrl + "' title='" + f.title + "' data-ref='" + f.perTracker + "' target='_blank'>");
            c.push("<img alt='" + f.title + "' src='" + (URLPrefix.statics + "/global/images/blank.gif") + "' shortimg='" + f.imgPath + "' wideimg='" + f.imgWidePath + "'/>");
            c.push("</a>");
            c.push("<div id='smallTopBanner' class='small_topbanner3' style='display: none;'>");
            c.push("<a class='small_topbanner3_side' href='" + g.imgJumpLinkUrl + "' title='" + g.title + "' data-ref='" + g.perTracker + "' target='_blank'>");
            c.push("<img alt='" + g.title + "' src='" + (URLPrefix.statics + "/global/images/blank.gif") + "' shortimg='" + g.imgPath + "' wideimg='" + g.imgWidePath + "'/>");
            c.push("<u style='display: none;'></u>");
            c.push("</a>");
            c.push("<a class='small_topbanner3_m' href='" + h.imgJumpLinkUrl + "' title='" + h.title + "' data-ref='" + h.perTracker + "' target='_blank'>");
            c.push("<img alt='" + h.title + "' src='" + (URLPrefix.statics + "/global/images/blank.gif") + "' shortimg='" + h.imgPath + "' wideimg='" + h.imgWidePath + "'/>");
            c.push("<u style='display: none;'></u>");
            c.push("</a>");
            c.push("<a class='small_topbanner3_side' href='" + k.imgJumpLinkUrl + "' title='" + k.title + "' data-ref='" + k.perTracker + "' target='_blank'>");
            c.push("<img alt='" + k.title + "' src='" + (URLPrefix.statics + "/global/images/blank.gif") + "' shortimg='" + k.imgPath + "' wideimg='" + k.imgWidePath + "'/>");
            c.push("<u style='display: none;'></u>");
            c.push("</a>");
            c.push("</div>");
            c.push("<span class='index_topbanner_fold'>收起<s></s></span>");
            c.push("</div>");
            $("#global_top_bar").after(c.join(""));
            $("#topCurtain").data("preloadFlag", 1)
        }
    };
    var E = function(a, g) {
        var h = "INDEX_NEWBANNER_ZHEN" + a + "_DATU";
        var b = "INDEX_NEWBANNER_ZHEN" + a + "_XIAOTU";
        var c = D(g, h, 1);
        var d = D(g, b, 1);
        var e = D(g, b, 2);
        var f = D(g, b, 3);
        if (c != null || d != null || e != null || f != null) {
            return {big: c,small1: d,small2: e,small3: f}
        }
        return null
    };
    var y = function(c, e) {
        var f = $("#promo_show");
        var a = $(".promo_wrapper ol li[flag=" + c + "]", f);
        if (a != null && a.size() > 0) {
            for (var d = 0; d < a.size(); d++) {
                var b = $(a[d]);
                if (b.data("preloadFlag")) {
                    return
                }
                b.data("preloadFlag", 1);
                var g = e.big;
                var h = e.small1;
                var k = e.small2;
                var l = e.small3;
                if (g != null) {
                    B(b.children("a"), g);
                    z(b.children("a").find("img"), g)
                }
                if (h != null) {
                    B(b.find(".mini_promo a").eq(0), h);
                    z(b.find(".mini_promo a img").eq(0), h)
                }
                if (k != null) {
                    B(b.find(".mini_promo a").eq(1), k);
                    z(b.find(".mini_promo a img").eq(1), k)
                }
                if (l != null) {
                    B(b.find(".mini_promo a").eq(2), l);
                    z(b.find(".mini_promo a img").eq(2), l)
                }
            }
        }
    };
    var x = function() {
        var b = $("#preloadAdvsData").data("advsData");
        if (b != null) {
            var a = G();
            var e = C(b);
            if (e.type != 0 && e.data != null && e.type > a) {
                if (e.type == 4) {
                    t(a, e.data)
                } else {
                    if (e.type == 3) {
                        H(a, e.data)
                    } else {
                        if (e.type == 2) {
                            F(a, e.data)
                        } else {
                            if (e.type == 1) {
                                v(a, e.data)
                            }
                        }
                    }
                }
            }
            for (var d = 1; d <= 10; d++) {
                var c = E(d, b);
                if (c != null) {
                    y(d, c)
                }
            }
        }
    };
    if (w != null) {
        $("#preloadAdvsData").data("advsData", w);
        x()
    }
};
YHD.HomePage.initAjaxReplaceAdvertise = function() {
    var n = $("#ajaxReplaceAdvCodesData");
    var u = n.val();
    var x = (u && u.length > 0) ? u.split(",") : [];
    var s = (typeof currSiteId == "undefined") ? 1 : currSiteId;
    var p = $.cookie("provinceId");
    var r = "";
    var w = function(a, c) {
        var f = null;
        var b = a[c];
        var g = b != null ? b : [];
        for (var d = 0; d < g.length; d++) {
            var e = g[d];
            if (e.biddingAdType == 7 && e.commonScreenImgUrl && e.wideScreenImgUrl) {
                f = e;
                break
            }
        }
        return f
    };
    var v = function(a, b) {
        a.attr("href", b.landingPage).attr("title", b.text).attr("data-ref", "")
    };
    var t = function(a, b) {
        a.attr("alt", b.text).attr("src", isWidescreen ? b.wideScreenImgUrl : b.commonScreenImgUrl);
        if (a.attr("shortimg") != null) {
            a.attr("shortimg", b.commonScreenImgUrl)
        }
        if (a.attr("wideimg") != null) {
            a.attr("wideimg", b.wideScreenImgUrl)
        }
        if (a.attr("si") != null) {
            a.attr("si", b.commonScreenImgUrl)
        }
        if (a.attr("wi") != null) {
            a.attr("wi", b.wideScreenImgUrl)
        }
        if (a.attr("original") != null) {
            a.attr("original", b.commonScreenImgUrl)
        }
    };
    var o = function() {
        if (n.size() == 0) {
            return
        }
        var a = n.data("advsData");
        var d = n.data("doneAdvCodes") != null ? n.data("doneAdvCodes").split(",") : [];
        if (a != null) {
            for (var f = 0; f < x.length; f++) {
                var h = w(a, x[f]);
                var c = false;
                for (var g = 0; g < d.length; g++) {
                    if (d[g] == x[f]) {
                        c = true;
                        break
                    }
                }
                if (!c && h != null) {
                    var e = $("body a[data-advId=" + h.regionId + "]");
                    var b = $("body img[data-advId=" + h.regionId + "]");
                    if (e.size() > 0) {
                        v(e, h);
                        t(b, h);
                        d.push(x[f]);
                        n.data("doneAdvCodes", d.join(","))
                    }
                }
            }
        }
    };
    var q = function(b, c) {
        var d = "http://p4p.yhd.com/advdolphin/external/saleTypeWeightAd?callback=?";
        var a = {mcSiteId: s,provinceId: p,codes: b,categoryIds: c,screenType: isWidescreen ? "1" : "2"};
        $.getJSON(d, a, function(g) {
            if (g && g.status == 1) {
                var f = g.value;
                if (f) {
                    var e = n.data("advsData");
                    if (e == null) {
                        n.data("advsData", f)
                    } else {
                        e = $.extend(e, f);
                        n.data("advsData", e)
                    }
                    o()
                }
            }
        })
    };
    YHD.HomePage.runAjaxReplaceAdvertise = o;
    window.ajaxReplaceAdvertiseTimeoutHandler = setTimeout(function() {
        var b = [];
        for (var a = 0; a < x.length; a++) {
            b.push(x[a]);
            if (b.length >= 20) {
                q(b.join(","), r);
                b = []
            }
        }
        if (b.length > 0) {
            q(b.join(","), r)
        }
    }, 1000)
};
jQuery(function() {
    YHD.HomePage.initPreloadAdvertise();
    YHD.HomePage.init();
    YHD.HomePage.initLunbo();
    YHD.HomePage.initFloor();
    scrollToTop();
    YHD.HomePage.initTuanTab();
    YHD.HomePage.initNewsTab();
    YHD.HomePage.initPromotTab();
    YHD.HomePage.reflushGrouponData();
    YHD.HomePage.initDigitTab();
    YHD.HomePage.initUnionSiteAndIE6UpdateMsg();
    YHD.HomePage.initBaifendian();
    YHD.HomePage.initAppreciateAdvertise();
    YHD.HomePage.initAjaxReplaceAdvertise()
});
(function(c) {
    var d = window.loli || (window.loli = {});
    d.scroll = function(l, b) {
        var j = "";
        var i = b || 200;
        var k = i - 20;
        c(window).scroll(function() {
            setTimeout(function() {
                a()
            }, i);
            j = new Date().getTime()
        });
        function a() {
            if ((new Date().getTime() - j) >= k) {
                l();
                j = new Date().getTime()
            }
        }
    }
})(jQuery);
(function(c) {
    var b = function(f) {
        var e = f, d = {lazyImg: {ltime: "2000",lnum: "5",load: true,indexLoad: false,scrollLoad: true,attr: "original",wideAttr: null,hfix: 100}};
        c.extend(d, e);
        this.param = d
    };
    b.prototype = {constructor: b,isBusy: false,doc: document,imgArray: [],wideAttr: null,lazyImg: function(d, h) {
            var i = this, f = i.param.lazyImg, g, e = d;
            if (h) {
                i.param.lazyImg = c.extend(f, h)
            }
            if (e instanceof c) {
                g = e
            } else {
                if (c.isArray(e)) {
                    e = c(e.join(","))
                } else {
                    e = c(e) || c("body")
                }
            }
            if (f.wideAttr) {
                wideAttr = f.wideAttr;
                i.imgArray = e.find("img[" + f.attr + "],img[" + wideAttr + "]")
            } else {
                i.imgArray = e.find("img[" + f.attr + "]")
            }
            if (f.indexLoad) {
                i._lazyImg(i.imgArray, f)
            }
            if (f.scrollLoad) {
                i._iniLazy(function() {
                    if (i.imgArray.length == 0) {
                        return g
                    }
                    i._lazyImg(i.imgArray, f)
                })
            }
            if (f.load) {
                i._loadImg(e)
            }
            return d
        },_loadImg: function(f) {
            var d = this, i = d.param.lazyImg, h = i.attr, g = i.ltime, e = i.lnum;
            (function(m, o, k, j, n) {
                var l = setInterval(function() {
                    if (m.isBusy) {
                        return false
                    }
                    var p = m.imgArray;
                    var q = p.length;
                    if (q > n) {
                        m._imgLoad(p, 0, n, k)
                    } else {
                        if (q > 0) {
                            m._imgLoad(p, 0, q, k)
                        } else {
                            clearInterval(l)
                        }
                    }
                }, j)
            })(d, f, h, g, e)
        },_lazyImg: function(i, g) {
            var e = g.attr, d = i.length, k = this, h = 0, f = 1;
            k.isBusy = true;
            var j = k._pageTop();
            k._imgLoad(k.imgArray, h, d, e, j, g.hfix);
            k.isBusy = false
        },_imgLoad: function(g, j, n, e, m, o) {
            var l = this;
            if (m) {
                for (var f = j; f < n; f++) {
                    var h = c(g[f]);
                    var d = jQuery(window).height() + o;
                    if (h.offset().top < (m + o) && (m - h.offset().top) < d) {
                        l._renderImg(h, e);
                        delete g[f]
                    }
                }
            } else {
                for (var f = j; f < n; f++) {
                    var h = c(g[f]);
                    l._renderImg(h, e);
                    delete g[f]
                }
            }
            var k = new Array();
            for (var f = 0; f < g.length; f++) {
                if (g[f] != null) {
                    k.push(g[f])
                }
            }
            l.imgArray = k
        },_renderImg: function(e, d) {
            var f = e;
            if (typeof wideAttr != "undefined" && wideAttr != null && f.attr(wideAttr)) {
                f.attr("src", f.attr(wideAttr));
                f.removeAttr(d)
            } else {
                f.attr("src", f.attr(d));
                f.removeAttr(d)
            }
        },_iniLazy: function(d) {
            var e = this;
            loli.delay(window, "scroll", function() {
                if (!e.isBusy) {
                    e.isBusy = true;
                    return true
                } else {
                    return false
                }
            }, function() {
                d()
            }, 50)
        },_pageTop: function() {
            var f = this, e = f.doc, d = e.documentElement;
            return d.clientHeight + Math.max(d.scrollTop, e.body.scrollTop)
        }};
    var a = new b();
    c.fn.extend({lazyImg: function(e) {
            var d = new b();
            return d.lazyImg(this, e)
        }})
})(jQuery);
(function(c) {
    var d = function(a) {
        var g = a, b = URLPrefix.busystock ? URLPrefix.busystock : "http://gps.yihaodian.com", h = "/busystock/restful/truestock";
        _setting = {attr: "productid",busystock_url: b + h,busystockAttr: "productIds",lazyLoadDelay: 500,priceCounter: 30,load: true,maxNum: 200,oneOffLoad: false,indexLoad: false,scrollLoad: true,hfix: 100,callbackHtml: null};
        c.extend(_setting, g);
        this.param = _setting
    };
    d.prototype = {constructor: d,isBusy: false,doc: document,priceArray: [],lazyPrice: function(m, o) {
            var a = this, p = a.param;
            if (o) {
                a.param = c.extend(p, o)
            }
            var b = m, k = p.attr, n = p.busystock_url, l = p.maxNum;
            if (b instanceof c) {
                a.priceArray = m.find("[" + k + "]").get()
            } else {
                if (c.isArray(b)) {
                    a.priceArray = b
                } else {
                    a.priceArray = c(m).find("[" + k + "]").get()
                }
            }
            if (p.oneOffLoad) {
                a._flushPrice(a.priceArray, k, n, p.busystockAttr, l);
                return m
            }
            if (p.indexLoad) {
                a._lazyPrice(a.imgArray, p)
            }
            if (p.scrollLoad) {
                a._iniLazy(function() {
                    if (a.priceArray.length == 0) {
                        return m
                    }
                    a._lazyPrice(a.priceArray, p)
                })
            }
            if (p.load) {
                a._loadPrice()
            }
            return m
        },_loadPrice: function() {
            var o = this, m = o.param, b = m.attr, l = m.busystock_url, n = m.busystockAttr, p = m.maxNum, a = m.lazyLoadDelay, k = m.priceCounter;
            (function(s, h, e, j, t, f, i) {
                var g = setInterval(function() {
                    if (s.isBusy) {
                        return false
                    }
                    var r = s.priceArray;
                    var q = r.length;
                    if (q > i) {
                        s._priceLoad(r, h, e, j, 0, i, t)
                    } else {
                        if (q > 0) {
                            s._priceLoad(r, h, e, j, 0, q, t)
                        } else {
                            clearInterval(g)
                        }
                    }
                }, f)
            })(o, b, l, n, p, a, k)
        },_lazyPrice: function(s, a) {
            var t = a.attr, p = s.length, m = a.busystock_url, n = a.busystockAttr, b = a.maxNum, r = this, q = 0;
            r.isBusy = true;
            var o = r._pageTop() + a.hfix;
            r._priceLoad(s, t, m, n, q, p, b, o);
            r.isBusy = false
        },_priceLoad: function(b, y, x, z, B, i, t, r) {
            var v = this, w = b.length;
            if (w == 0) {
                return
            }
            var A = new Array();
            if (r) {
                for (var s = B; 
                s < i; s++) {
                    var a = c(b[s]);
                    if (a.offset().top < r) {
                        A.push(a);
                        delete b[s]
                    }
                }
            } else {
                for (var s = B; s < i; s++) {
                    var a = c(b[s]);
                    A.push(a);
                    delete b[s]
                }
            }
            v._flushPrice(A, y, x, z, t);
            var u = new Array();
            for (var s = 0; s < b.length; s++) {
                if (b[s] != null) {
                    u.push(b[s])
                }
            }
            v.priceArray = u
        },_iniLazy: function(b) {
            var a = this;
            window.scrollTo(0, 0);
            c(window).bind("scroll", function() {
                if (!a.isBusy) {
                    b()
                } else {
                }
            })
        },_pageTop: function() {
            var f = this, a = f.doc, b = a.documentElement;
            return b.clientHeight + Math.max(b.scrollTop, a.body.scrollTop)
        },_flushPrice: function(L, A, K, j, I) {
            var y = this, b = y.param, J = b.callbackHtml;
            if (L && L.length > 0) {
                var B = L.length, e = 0, a, C = 1;
                if (B < I) {
                    a = B
                } else {
                    C = (B - 1) / I + 1
                }
                var D = jQuery.cookie("provinceId");
                if (!D) {
                    return
                }
                var G = "?mcsite=" + currBsSiteId + "&provinceId=" + D;
                var E = {};
                for (var H = 0; H < C; H++) {
                    if (H > 0) {
                        e = I * H;
                        a = e + I;
                        if (a > B) {
                            a = B
                        }
                    }
                    E = {};
                    for (var F = e; 
                    F < a; F++) {
                        var z = jQuery(L[F]);
                        G += "&" + j + "=" + z.attr(A);
                        if (!E[z.attr(A)]) {
                            E[z.attr(A)] = []
                        }
                        E[z.attr(A)].push(z)
                    }
                    try {
                        jQuery.getJSON(K + G + "&callback=?", function(f) {
                            if (f == null || f == "") {
                                return
                            }
                            jQuery.each(f, function(g, k) {
                                var h = E[k.productId];
                                if (h) {
                                    jQuery.each(h, function(l, m) {
                                        if (J) {
                                            jQuery(m).html(J(k, m)).removeAttr(A)
                                        } else {
                                            if (currSiteId == 2) {
                                                jQuery(m).text("¥" + k.productPrice).removeAttr(A)
                                            } else {
                                                if (h) {
                                                    if (globalShowMarketPrice == 1) {
                                                        var n = "<strong>¥" + k.productPrice + "</strong>";
                                                        n += "<del>¥" + k.marketPrice + "</del>";
                                                        jQuery(m).html(n).removeAttr(A)
                                                    } else {
                                                        var n = "<strong>¥" + k.productPrice + "</strong>";
                                                        if (k.curPriceType && k.curPriceType == 2 && k.yhdPrice) {
                                                            n += "<del>¥" + k.yhdPrice + "</del>"
                                                        }
                                                        jQuery(m).html(n).removeAttr(A)
                                                    }
                                                }
                                            }
                                        }
                                    })
                                }
                            })
                        })
                    } catch (i) {
                    }
                }
            }
        }};
    c.fn.extend({lazyPrice: function(a) {
            var b = new d();
            return b.lazyPrice(this, a)
        }})
})(jQuery);
(function(b) {
    var a = function(e) {
        var d = e, c = {activeLoadTime: 2000,load: true,activeLoadNum: 1,hfix: 100,callback: null,attr: "lazyLoad_textarea",flushPrice: true,flushPriceAttr: "productid",indexLoad: false,scrollLoad: true};
        b.extend(c, d);
        this.param = c
    };
    a.prototype = {constructor: a,doc: document,areaArray: [],lazyDom: function(f, e) {
            var c = this, d = c.param, g = f;
            if (e) {
                c.param = b.extend(d, e)
            }
            c.areaArray = c._getJqueryDomArray(g, d);
            if (d.indexLoad) {
                c._domScrollLoad(c.areaArray, d)
            }
            if (d.scrollLoad) {
                c._loadScrollDom(function() {
                    if (c.areaArray.length == 0) {
                        return
                    }
                    c._domScrollLoad(c.areaArray, d)
                })
            }
            if (d.load) {
                c._loadActiveDom(c.areaArray, d)
            }
        },_loadActiveDom: function(g, d) {
            var h = this, c = d, i = c.activeLoadTime, f = g;
            var e = setInterval(function() {
                var j = f.length;
                if (j == 0) {
                    clearInterval(e);
                    return
                }
                h._domActiveLoad(f, c)
            }, i)
        },_loadScrollDom: function(c) {
            loli.scroll(function() {
                c()
            }, 50)
        },_domScrollLoad: function(d, f) {
            var h = this, f = h.param, c = [];
            for (var j = 0, g = d.length; j < g; j++) {
                var e = h._getJqueryDom(d[j]);
                if (h.isInCurrScreen(e)) {
                    h._rendDom(e, f)
                } else {
                    c.push(e)
                }
            }
            h.areaArray = c
        },_domActiveLoad: function(f, d) {
            var j = this, c = d, g = f, k = g.length, h = Math.min(c.activeLoadNum, k);
            for (var e = 0; e < h; e++) {
                j._rendDom(j._getJqueryDom(g.shift()), c)
            }
        },_rendDom: function(k, d) {
            var i = k, f = d, e = f.attr, h = i.attr(e), j = b("#" + h), g = f.flushPrice, c = f.flushPriceAttr;
            i.html(j.val());
            i.removeAttr(e);
            if (g) {
                i.lazyPrice({attr: c,oneOffLoad: true})
            }
            if (f.callback) {
                f.callback.call(i)
            }
        },isInCurrScreen: function(f) {
            var h = this, i = f, c = h.doc, j = c.documentElement, g = h.param, d = g.hfix, e = Math.max(j.scrollTop, c.body.scrollTop), k = j.clientHeight + e;
            if (i) {
                return (i.offset().top < k + d) && (i.offset().top > e - d)
            }
            return false
        },_getJqueryDomArray: function(d, c) {
            var e = [], f = c.attr;
            if (d instanceof b) {
                e = d.find("[" + f + "]").get()
            } else {
                if (b.isArray(d)) {
                    e = d;
                    return e
                } else {
                    d = b(d);
                    e = d.find("[" + f + "]").get()
                }
            }
            if (e.length == 0) {
                if (d.attr(f)) {
                    e.push(d)
                }
            }
            return e
        },_getJqueryDom: function(c) {
            if (!c) {
                return c
            }
            if (c instanceof b) {
                return c
            }
            return b(c)
        }};
    b.fn.extend({lazyDom: function(d) {
            var c = new a();
            return c.lazyDom(this, d)
        }})
})(jQuery);
var busystcok = URLPrefix.busystock ? URLPrefix.busystock : "http://gps.yihaodian.com";
jQuery((function(b) {
    YHD = YHD || {};
    YHD.HomePagelazyLoade = new function() {
        var i = 500;
        var g = false;
        var a = 30;
        var j = this;
        this.lazyPrice;
        var h = function() {
            runfunctions([], [j.loadPrice], j);
            if (j.lazyPrice && !j.lazyPrice.length) {
                b(window).unbind("scroll", h)
            }
        };
        this.init = function() {
            h();
            b(window).bind("scroll", h)
        };
        this.pageTop = function() {
            return document.documentElement.clientHeight + Math.max(document.documentElement.scrollTop, document.body.scrollTop)
        };
        this.loadPrice = function() {
            if (g) {
                return
            }
            g = true;
            var f = 0;
            try {
                if (!b.cookie("provinceId")) {
                    return
                }
                var p = this.pageTop();
                if (!this.lazyLoadPrice) {
                    this.lazyLoadPrice = b("[productid]").get()
                }
                var c = "?mcsite=" + currBsSiteId + "&provinceId=" + b.cookie("provinceId");
                var e = [];
                var o = {};
                b.each(this.lazyLoadPrice, function(l, k) {
                    if (b(k).attr("productid") && a > f && b(k).offset().top <= p + 100) {
                        c += "&productIds=" + b(k).attr("productid");
                        f++;
                        if (o[b(k).attr("productid")]) {
                            o[b(k).attr("productid")].add(k)
                        } else {
                            o[b(k).attr("productid")] = b(this)
                        }
                    } else {
                        e.push(k)
                    }
                });
                this.lazyLoadPrice = e;
                if (f > 0) {
                    try {
                        var n = busystcok + "/busystock/restful/truestock";
                        b.getJSON(n + c + "&callback=?", function(k) {
                            if (k == null || k == "") {
                                return
                            }
                            b.each(k, function(t, l) {
                                var u = o[l.productId];
                                if (u) {
                                    var m = "&yen;" + l.productPrice;
                                    u.html(m).removeAttr("productid")
                                }
                            })
                        })
                    } catch (d) {
                    }
                }
            } catch (d) {
                setTimeout("YHD.HomePagelazyLoade.loadPrice()", i)
            }
            if (f >= a) {
                setTimeout("YHD.HomePagelazyLoade.loadPrice()", i)
            }
            g = false
        }
    }
})(jQuery));
jQuery(function() {
    jQuery("body").lazyImg({indexLoad: true,wideAttr: isWidescreen ? "wideimg" : "shortimg"});
    jQuery("#needLazyLoad").lazyDom({load: false,hfix: 500,flushPrice: false,callback: function() {
            var a = this;
            var b = $(a);
            b.find(".small_img").jQFade();
            h();
            var d = b.find(".promotion_banner a").attr("data-ref");
            var e = jQuery.cookie("provinceId");
            if (!e) {
                e = 0
            }
            if (jQuery.trim("leftPromBannerTK") != "" && e != 0) {
                recordTrackInfoWithType("1", d + "_" + e + "_" + 0, "ad.dolphin.cpt")
            }
            if (b.hasClass("floor8")) {
                b.find(".brand_logo_white").each(function(n, m) {
                    YHD.HomePage.Tools.rate(45, m)
                });
                b.find(".brand_logo_gray").each(function(n, m) {
                    YHD.HomePage.Tools.rate(45, m)
                });
                var c = !-[1, ];
                if (c) {
                    jQuery(".brand_logo_gray").remove();
                    jQuery(".discount", "gray_logo1").remove();
                    jQuery(".discount", "gray_logo2").remove();
                    jQuery(".discount", "gray_logo3").remove()
                }
                return
            }
            j(b);
            b.lazyPrice({attr: "productId",oneOffLoad: true,callbackHtml: function(o, n) {
                    var p = [];
                    if (typeof n != "undefined" && jQuery(n).length > 0 && jQuery(n).get(0).tagName == "P") {
                        p.push("<span>&yen;" + o.productPrice + "</span>");
                        if (typeof globalShowMarketPrice != "undefined" && globalShowMarketPrice == 1) {
                            p.push("<del>" + o.marketPrice + "</del>")
                        } else {
                            if (o.curPriceType != 1 && o.yhdPrice > 0) {
                                p.push("<del>" + o.yhdPrice + "</del>")
                            }
                        }
                    }
                    if (typeof n != "undefined" && jQuery(n).length > 0 && jQuery(n).get(0).tagName == "SPAN") {
                        p.push("&yen;" + o.productPrice)
                    }
                    return p.join("")
                }});
            g(b);
            if (b.hasClass("mod_index_floor") && !b.data("floor_switch")) {
                loli.ui.switchable(b.find(".right_module"), {autoPlay: true}, i);
                b.data("floor_switch", true)
            }
        }});
    var i = function() {
        jQuery.each(this.toPanels.find("img"), function() {
            var a = $(this);
            if (a.attr("original")) {
                a.attr("src", a.attr("original"));
                a.removeAttr("original")
            }
        })
    }, j = function(a) {
        var b = a.find(".lazyLoadLoucengTagContent"), d = a.hasClass("floor1") || a.hasClass("floor2"), n = b.val(), o = $.parseJSON(n), e = (a.hasClass("floor3") || a.hasClass("floor4") || a.hasClass("floor6") || a.hasClass("floor7")) ? "clearfix pro_list" : "clearfix", p = a.hasClass("floor3") || a.hasClass("floor4") || a.hasClass("floor5") || a.hasClass("floor6") || a.hasClass("floor7"), c = f(o, d, e, p);
        a.find(".right_tab_content").find(".tab_content_wrap").after(c)
    }, f = function(d, b, c, e) {
        if (!d) {
            return
        }
        var a = {datas: d,showBigPic: b,cssClass: c,showPriceOnPic: e,urlPrefix: URLPrefix,globalShowMarketPrice: typeof globalShowMarketPrice == "undefined" ? 0 : globalShowMarketPrice};
        return template.render("loucengTagContent", a)
    };
    function g(a) {
        if (a.hasClass("floor_height1")) {
            a.find(".right_tab").find("li:gt(" + 2 + ")").addClass("higher")
        } else {
            if (a.hasClass("floor_height4")) {
                a.find(".right_tab").find("li:gt(" + 1 + ")").addClass("higher")
            }
        }
    }
    function h() {
        if (typeof YHD != undefined && typeof YHD.HomePage != undefined && typeof YHD.HomePage.runAjaxReplaceAdvertise != undefined) {
            YHD.HomePage.runAjaxReplaceAdvertise()
        }
    }
});
function getQueryStringRegExp(a) {
    var b = new RegExp("(^|\\?|&)" + a + "=([^&]*)(\\s|&|$)", "i");
    if (b.test(location.href)) {
        return unescape(RegExp.$2.replace(/\+/g, " "))
    }
    return ""
}
var referrer = document.referrer ? document.referrer : "";
var referrerDomain = referrer.match(/http[s]?:\/\/([^\/]+)/);
var ref = getQueryStringRegExp("tracker_u");
var uid = getQueryStringRegExp("uid");
var websiteid = getQueryStringRegExp("website_id");
var utype = getQueryStringRegExp("tracker_type");
var adgroupKeywordID = getQueryStringRegExp("adgroupKeywordID");
var edmEmail = getQueryStringRegExp("emailId");
var expire_time = new Date((new Date()).getTime() + 30 * 24 * 3600000).toGMTString();
var expire_time2 = new Date((new Date()).getTime() + 30 * 24 * 3600000).toGMTString();
var expire_time3 = new Date((new Date()).getTime()).toGMTString();
var expire_time_wangmeng = new Date((new Date()).getTime() + 1 * 24 * 3600000).toGMTString();
if (ref && (referrerDomain == null || referrerDomain[1].indexOf("union.yhd.com") != -1 || referrerDomain[1].indexOf(no3wUrl) == -1)) {
    if (ref != "" && !isNaN(ref)) {
        document.cookie = "unionKey=" + ref + ";expires=" + expire_time_wangmeng + ";domain=." + no3wUrl + ";path=/"
    }
}
if (adgroupKeywordID) {
    if (adgroupKeywordID != "") {
        document.cookie = "adgroupKeywordID=" + adgroupKeywordID + ";expires=" + expire_time_wangmeng + ";domain=." + no3wUrl + ";path=/"
    }
}
if (utype) {
    if (utype != "") {
        document.cookie = "unionType=" + utype + ";expires=" + expire_time2 + ";domain=." + no3wUrl + ";path=/"
    }
}
if (uid) {
    document.cookie = "uid=" + uid + ";expires=" + expire_time + ";domain=." + no3wUrl + ";path=/"
}
if (websiteid) {
    document.cookie = "websiteid=" + websiteid + ";expires=" + expire_time + ";domain=." + no3wUrl + ";path=/"
}
if (edmEmail) {
    document.cookie = "edmEmail=" + edmEmail + ";domain=." + no3wUrl + ";path=/"
}
;
var refer = document.referrer ? document.referrer : "";
if (refer != "") {
    refer = encodeURIComponent(refer)
}
var pars = document.location.search;
var input = new Object();
if (pars && pars.indexOf("?") == 0 && pars.length > 1) {
    pars = pars.substr(1);
    var list = pars.split("&");
    for (var n = 0; n < list.length; n++) {
        var item = list[n].split("=");
        if (item.length == 2) {
            input[item[0]] = item[1]
        }
    }
}
var tracker_u = input.tracker_u ? input.tracker_u : "";
var tracker_type = input.tracker_type ? input.tracker_type : "";
var tracker_pid = input.tracker_pid ? input.tracker_pid : "";
var tracker_src = input.tracker_src ? input.tracker_src : "";
var adgroupKeywordID = input.adgroupKeywordID ? input.adgroupKeywordID : "";
if (refer != "" && "" == tracker_u) {
    tracker_type = "0"
}
if (jQuery.cookie("unionKey")) {
    trackerContainer.addParameter(new Parameter("tracker_u", jQuery.cookie("unionKey")))
}
trackerContainer.addParameter(new Parameter("tracker_src", tracker_src));
var info_refer = document.referrer ? document.referrer : "";
if (info_refer != "") {
    info_refer = encodeURIComponent(info_refer)
}
trackerContainer.addParameter(new Parameter("infoPreviousUrl", info_refer));
trackerContainer.addParameter(new Parameter("infoTrackerSrc", tracker_src));
if (jQuery.cookie("yihaodian_uid")) {
    trackerContainer.addParameter(new Parameter("endUserId", jQuery.cookie("yihaodian_uid")))
}
if (jQuery.cookie("adgroupKeywordID")) {
    trackerContainer.addParameter(new Parameter("adgroupKeywordID", jQuery.cookie("adgroupKeywordID")))
}
if (jQuery.cookie("abtest")) {
    trackerContainer.addParameter(new Parameter("extField6", jQuery.cookie("abtest")))
}
if (jQuery.cookie("extField8")) {
    trackerContainer.addParameter(new Parameter("extField8", jQuery.cookie("extField8")))
}
if (jQuery.cookie("extField9")) {
    trackerContainer.addParameter(new Parameter("extField9", jQuery.cookie("extField9")))
}
if (jQuery.cookie("extField10")) {
    trackerContainer.addParameter(new Parameter("extField10", jQuery.cookie("extField10")))
}
var sendTrackerCookie = "";
if (jQuery.cookie("msessionid")) {
    sendTrackerCookie = "msessionid:" + jQuery.cookie("msessionid")
}
if (jQuery.cookie("uname")) {
    sendTrackerCookie += ",uname:" + jQuery.cookie("uname")
}
if (jQuery.cookie("unionKey")) {
    sendTrackerCookie += ",unionKey:" + jQuery.cookie("unionKey")
}
if (jQuery.cookie("unionType")) {
    sendTrackerCookie += ",unionType:" + jQuery.cookie("unionType")
}
if (jQuery.cookie("tracker")) {
    sendTrackerCookie += ",tracker:" + jQuery.cookie("tracker")
}
if (jQuery.cookie("LTINFO")) {
    sendTrackerCookie += ",LTINFO:" + jQuery.cookie("LTINFO")
}
trackerContainer.addParameter(new Parameter("cookie", sendTrackerCookie));
if (getQueryStringRegExp("fee")) {
    trackerContainer.addParameter(new Parameter("fee", getQueryStringRegExp("fee")))
}
trackerContainer.addParameter(new Parameter("provinceId", jQuery.cookie("provinceId")));
trackerContainer.addParameter(new Parameter("cityId", jQuery.cookie("cityId")));
var tracker_params = new Array();
function clearTrackPositionToCookie(c, a) {
    var b = new Date();
    b.setTime(b.getTime() - 10000);
    document.cookie = c + "=" + a + ";path=/;domain=." + no3wUrl + ";expires=" + b.toGMTString()
}
function clearCookieWithName(c, a) {
    var b = new Date();
    b.setTime(b.getTime() - 10000);
    document.cookie = c + "=" + a + ";path=/;domain=." + no3wUrl + ";expires=" + b.toGMTString()
}
function initHijack() {
	ilog('initHijack...');
    jQuery.ajax({async: false,url: trackerContainer.toUrl(),type: "GET",dataType: "jsonp",jsonp: "jsoncallback"})
}
jQuery(document).ready(function() {
	 ilog('###infoPageId');
    if (getCookie("linkPosition")) {
        linkPosition = getCookie("linkPosition");
        trackerContainer.addParameter(new Parameter("linkPosition", linkPosition));
        clearTrackPositionToCookie("linkPosition", linkPosition)
    }
    var e = window.loli || {};
    if (e && e.spm) {
        var f = loli.spm.getNewPageData();
        if (f && typeof (f) == "object") {
            var d = f.pageType;
            var b = f.pageId;
            var c = f.tc;
            var g = f.tp;
            var a = f.uniId;
            var i = d + "." + b + ".0.0.0." + a;
            ilog('###infoPageId');
            trackerContainer.addParameter(new Parameter("infoPageId", i));
            trackerContainer.addParameter(new Parameter("infoLinkId", c));
            trackerContainer.addParameter(new Parameter("infoModuleId", g))
        }
    }
    if (getCookie("pmInfoId")) {
        pmInfoId = getCookie("pmInfoId");
        trackerContainer.addParameter(new Parameter("extField7", pmInfoId));
        clearCookieWithName("pmInfoId", pmInfoId)
    }
    if (getCookie("productId")) {
        productId = getCookie("productId");
        trackerContainer.addParameter(new Parameter("productId", productId));
        clearCookieWithName("productId", productId)
    }
    var h = getCookie("edmEmail");
    if (h != null) {
        trackerContainer.addParameter(new Parameter("edmEmail", h))
    }
    initHijack()
});
function callTracker(a) {
    trackerContainer.addParameter(new Parameter("provinceId", a));
    trackerContainer.addParameter(new Parameter("cityId", jQuery.cookie("cityId")));
    jQuery.ajax({async: true,url: trackerContainer.toUrl(),type: "GET",dataType: "jsonp",jsonp: "jsoncallback"})
}
;
function addTrackerToEvent(c, a) {
    var b = "tk";
    if (a) {
        b = a
    }
    if (c instanceof jQuery) {
        c.find("a[" + b + "]").click(function() {
            var e = $(this), d = e.attr(b);
            if (d) {
                addTrackPositionToCookie("1", d)
            }
        })
    } else {
        $(c + " a[" + b + "]").each(function(d) {
            var e = this;
            $(e).click(function() {
                addTrackPositionToCookie("1", $(e).attr(b))
            })
        })
    }
}
;
var yhdHead = window.yhdHead = window.yhdHead || {};
yhdHead.topMenuImgLazyLoad = function() {
    jQuery("#wideScreenTabShowID li img").each(function() {
        jQuery(this).attr("src", function() {
            return jQuery(this).attr("original")
        }).removeAttr("original")
    });
    jQuery("#allCategoryHeader ul li h3 img").each(function() {
        jQuery(this).attr("src", function() {
            return jQuery(this).attr("original")
        }).removeAttr("original")
    })
};
yhdHead.newTopTabShow = function(b, a) {
    if (b > a) {
        jQuery("#wideScreenTabShowID li").each(function(c) {
            if (c == a - 1) {
                jQuery(this).addClass("kf")
            }
            if (c > a - 1) {
                jQuery(this).remove()
            }
        })
    }
};
yhdHead.oldTopTabShow = function(b, a) {
    if (b > a) {
        jQuery("#global_menu span").each(function(c) {
            if (c > a - 1) {
                jQuery(this).remove()
            }
        })
    }
};
yhdHead.dealWideNarrowScreen = function() {
    var a = screen.width >= 1280;
    if (currSiteId == 1) {
        var c = jQuery("#wideScreenTabShowID li").length;
        var b = jQuery("#global_menu span").length;
        if (!a) {
            yhdHead.newTopTabShow(c, 10);
            yhdHead.oldTopTabShow(b, 7)
        } else {
            if (isIndex) {
                if (isIndex == 1) {
                    yhdHead.newTopTabShow(c, 10)
                } else {
                    yhdHead.newTopTabShow(c, 10)
                }
            } else {
                yhdHead.newTopTabShow(c, 10)
            }
            yhdHead.oldTopTabShow(b, 7)
        }
    } else {
        var c = jQuery("#wideScreenTabShowID li").length;
        var b = jQuery("#global_menu span").length;
        if (!a) {
            yhdHead.newTopTabShow(c, 8);
            yhdHead.oldTopTabShow(b, 6)
        } else {
            if (isIndex) {
                if (isIndex == 1) {
                    yhdHead.newTopTabShow(c, 9)
                } else {
                    yhdHead.newTopTabShow(c, 8)
                }
            } else {
                yhdHead.newTopTabShow(c, 8)
            }
            yhdHead.oldTopTabShow(b, 6)
        }
    }
};
yhdHead.topMenuTrackInit = function() {
    jQuery("#wideScreenTabShowID li a[tk]").click(function() {
        var b = $(this), a = b.attr("tk");
        if (a) {
            addTrackPositionToCookie("1", a)
        }
    });
    jQuery("#global_menu span a[tk]").click(function() {
        var b = $(this), a = b.attr("tk");
        if (a) {
            addTrackPositionToCookie("1", a)
        }
    })
};
jQuery(function() {
    yhdHead.topMenuImgLazyLoad();
    yhdHead.topMenuTrackInit()
});
jQuery(function() {
    var b = location.search;
    if (b.indexOf("isAdvStatistics=1") > -1 && b.indexOf("advParams=") > -1) {
        $.getScript("http://adbackend.yihaodian.com/js/adv/advertising.js", function() {
            var d = document.createElement("link");
            d.type = "text/css";
            d.rel = "stylesheet";
            d.href = "http://adbackend.yihaodian.com/css/adv/tk.css";
            var a = document.getElementsByTagName("script")[0];
            a.parentNode.insertBefore(d, a)
        })
    }
});
var returnUrl = document.location.href;
var yhdPublicLogin = yhdPublicLogin || {};
var URLPrefix_passport = URLPrefix.passport;
yhdPublicLogin.checkLogin = function() {
    if (yhdPublicLogin.getCookie("ut")) {
        return true
    } else {
        return false
    }
};
yhdPublicLogin.getCookie = function(f) {
    var e = document.cookie.split(";");
    for (var g = 0; g < e.length; g++) {
        var h = e[g].split("=");
        if (h[0].replace(/(^\s*)|(\s*$)/g, "") == f) {
            return h[1]
        }
    }
    return ""
};
yhdPublicLogin.loadCssAndJs = function(h, f) {
    var e = "";
    var g = 0;
    if (typeof currVersionNum != "undefined") {
        g = currVersionNum
    }
    if (f == "js") {
        e = document.createElement("script");
        e.setAttribute("type", "text/javascript");
        e.setAttribute("charset", "UTF-8");
        e.setAttribute("src", h + "?" + g)
    } else {
        if (f == "css") {
            e = document.createElement("link");
            e.setAttribute("rel", "stylesheet");
            e.setAttribute("type", "text/css");
            e.setAttribute("href", h + "?" + g)
        }
    }
    if (typeof e != "undefined") {
        document.getElementsByTagName("head")[0].appendChild(e)
    }
};
yhdPublicLogin.showLoginDiv = function(q, o, m) {
    if (o && yhdPublicLogin.checkLogin()) {
        return
    }
    if (q) {
        var p = "";
        if (q.toLowerCase().indexOf("http") < 0) {
            var k = window.location.protocol;
            var j = window.location.host;
            var l = k + "//" + j;
            p = l
        }
        var r = p + q;
        returnUrl = r
    }
    try {
        passportLoginFrame(URLPrefix_passport, null, function(b) {
            try {
                if (returnUrl) {
                    window.location.href = returnUrl
                } else {
                    window.location.reload(true)
                }
            } catch (a) {
            }
        }, m)
    } catch (n) {
    }
};
yhdPublicLogin.showLoginDivNone = function(k, j, e, h, l) {
    if (j && yhdPublicLogin.checkLogin()) {
        return
    }
    try {
        passportLoginFrame(k, e, h, l)
    } catch (i) {
    }
};
yhdPublicLogin.showTopLoginInfo = function() {
    try {
        writeHeaderContent()
    } catch (b) {
    }
};
jQuery(document).ready(function() {
    var b = "";
    if (URLPrefix && URLPrefix.statics) {
        b = URLPrefix.statics
    } else {
        if (currSiteId && currSiteId == 2) {
            b = "http://image.111.com.cn/statics"
        } else {
            b = "http://image.yihaodianimg.com/statics"
        }
    }
    yhdPublicLogin.loadCssAndJs(b + "/global/css/global_yhdLib.css", "css");
    yhdPublicLogin.loadCssAndJs(b + "/global/js/global_yhdLib.js", "js");
    yhdPublicLogin.loadCssAndJs(URLPrefix_passport + "/front-passport/passport/js/login_frame_client.js", "js")
});
var jsTopbarFed = {ieLower: $.browser.msie && $.browser.version == 6 || false,isWidescreen: screen.width >= 1280,userNameMax: function() {
        if (jsTopbarFed.ieLower) {
            var a = jQuery("#user_name");
            var b = a.width();
            if (jsTopbarFed.isWidescreen) {
                if (b > 215) {
                    a.css("width", "215")
                }
            } else {
                if (b > 138) {
                    a.css("width", "138")
                }
            }
        }
    },bindHoverEvent: function() {
        jQuery("#global_top_bar").delegate("[data-addClass]", "mouseenter", function() {
            var c = jQuery(this);
            var b = c.attr("data-addClass");
            c.addClass(b);
            a(c)
        });
        function a(d) {
            var c = d.attr("id");
            if (c == "hd_mobile_devic") {
                var b = $("#hd_mobile_list").find("span img");
                jQuery.each(b, function(e, g) {
                    var f = jQuery(g);
                    if (f.attr("original")) {
                        f.attr("src", f.attr("original"));
                        f.removeAttr("original")
                    }
                });
                if (b.length > 0) {
                    $("p", "#hd_mobile_list").hover(function() {
                        $(this).addClass("cur")
                    }, function() {
                        $(this).removeClass("cur")
                    })
                }
            }
            if (d.has(".hd_weixin_show").length) {
                jsTopbarFed.weixinTextMax();
                d.lazyImg()
            }
        }
    },weixinTextMax: function() {
        if (jsTopbarFed.ieLower) {
            var a = $("p", ".hd_weixin_show").height(), b = 36;
            if (a > b) {
                $("p", ".hd_weixin_show").css("height", b)
            }
        }
    },bindHoverOutEvent: function() {
        jQuery("#global_top_bar").delegate("[data-addClass]", "mouseleave", function() {
            var b = jQuery(this);
            var a = b.attr("data-addClass");
            b.removeClass(a)
        })
    },setNoticeTop: function(a) {
        var b = jQuery(a);
        if (b[0] && jQuery("#hd_head_skin")[0]) {
            var c = jQuery("#topbanner");
            if (c[0]) {
                c.find("img").load(function() {
                    b.css("top", c.height())
                })
            } else {
                if (!jQuery("#topCurtain")[0]) {
                    b.css("top", 0)
                }
            }
        }
    },allSortOpen: function() {
        $("#allSortOuterbox", ".hd_nav_fixed").live("hover", function() {
            $(this).toggleClass("allsort_open");
            return false
        })
    },smallTopBannerHover: function() {
        var a = $("#smallTopBanner");
        if (a.length < 1) {
            a = $("#topbanner").find(".small_topbanner3")
        }
        if (a.length < 1) {
            return
        }
        a.delegate("a", "mouseover", function() {
            $(this).siblings("a").find("u").show()
        });
        a.delegate("a", "mouseout", function() {
            $(this).siblings("a").find("u").hide()
        })
    },closeNotice: function(a) {
        $("#hd_header_notice").delegate(".hd_notice_close", "click", function() {
            $(this).parents(".hd_header_notice").slideUp()
        })
    },loadFun: function() {
        jsTopbarFed.bindHoverEvent();
        jsTopbarFed.bindHoverOutEvent();
        jsTopbarFed.allSortOpen();
        jsTopbarFed.smallTopBannerHover();
        jsTopbarFed.closeNotice()
    }};
jQuery(document).ready(function() {
    jsTopbarFed.userNameMax();
    jsTopbarFed.loadFun()
});
(function(b) {
    var a = window.loli || (window.loli = {});
    a.timing = {timeToStr: function(f, d) {
            var c = [];
            for (var e in f) {
                if (f[e].value == -1) {
                    continue
                }
                c.push(f[e].name + "_" + f[e].value)
            }
            if (d) {
                c.push(d)
            }
            return (c.join("|"))
        },basicTime: function(g) {
            if (!window.performance) {
                return
            }
            var c = window.performance, f = c.timing, h = c.navigation, e = {redirectCount: {name: "RDTT",value: h.redirectCount},redirectTime: {name: "RDTM",value: f.redirectEnd - f.redirectStart},domainLookupTime: {name: "DMLKT",value: f.domainLookupEnd - f.domainLookupStart},connectTime: {name: "CONTT",value: f.connectEnd - f.connectStart},requestTime: {name: "REQT",value: f.responseStart - (f.requestStart || f.responseStart + 1)},responseTime: {name: "RSPT",func: function() {
                        var i = f.responseEnd - f.responseStart;
                        if (f.domContentLoadedEventStart) {
                            if (i < 0) {
                                i = 0
                            }
                        } else {
                            i = -1
                        }
                        return i
                    },value: -1},domParsingTime: {name: "DMPT",func: function() {
                        return f.domContentLoadedEventStart ? f.domInteractive - f.domLoading : -1
                    },value: -1},domLoadedTime: {name: "DMLT",func: function() {
                        if (f.loadEventStart) {
                            return f.loadEventStart - f.domInteractive
                        }
                        return f.domComplete ? f.domComplete - f.domInteractive : -1
                    },value: -1},winOnLoadTime: {name: "ONLOADT",func: function() {
                        return f.loadEventEnd ? f.loadEventEnd - f.loadEventStart : -1
                    },value: -1},pageLoadTime: {name: "PAGET",func: function() {
                        if (f.loadEventStart) {
                            return f.loadEventStart - f.fetchStart
                        }
                        return f.domComplete ? f.domComplete - f.fetchStart : -1
                    },value: -1},allLoadTime: {name: "ALLT",func: function() {
                        if (f.loadEventEnd) {
                            return f.loadEventEnd - f.navigationStart
                        }
                        return f.domComplete ? f.domComplete - f.navigationStart : -1
                    },value: -1},firstPaintTime: {name: "FPAINTT",func: function() {
                        var i = f.firstPaint || f.msFirstPaint || f.mozFirstPaint || f.webkitFirstPaint || f.oFirstPaint;
                        return i ? i - f.navigationStart : -1
                    },value: -1},beforeDomLoadingTime: {name: "BEFDMLT",func: function() {
                        return f.domLoading ? f.domLoading - f.navigationStart : -1
                    },value: -1},resourcesLoadedTime: {name: "RESLOADT",func: function() {
                        if (f.loadEventStart) {
                            return f.loadEventStart - f.domLoading
                        }
                        return f.domComplete ? f.domComplete - f.domLoading : -1
                    },value: -1},scriptRunTime: {name: "SCRIPTT",func: function() {
                        if (f.loadEventStart) {
                            return f.loadEventStart - f.domContentLoadedEventStart
                        }
                        return f.domComplete ? f.domComplete - f.domContentLoadedEventStart : -1
                    },value: -1},customInteractTime: {name: "CINTT",func: function() {
                        var j = window.global || (window.global = {});
                        var k = j.vars = (j.vars || {});
                        var i = j.vars.customInteractTime;
                        if (i) {
                            return i - window.performance.timing.navigationStart
                        } else {
                            return -1
                        }
                    },value: -1},interactTime: {name: "INTT",func: function() {
                        if (f.domContentLoadedEventStart) {
                            return f.domContentLoadedEventStart - f.navigationStart
                        }
                        return -1
                    },value: -1}};
            for (var d in e) {
                if (e[d].value == -1 && typeof e[d].func == "function") {
                    e[d].value = e[d].func()
                }
            }
            return this.timeToStr(e, g)
        },eventHandleTime: function(f) {
            try {
                var d = [];
                if (typeof f == "undefined") {
                    return false
                } else {
                    if (f instanceof Array) {
                        var c = false;
                        for (var j = 0; j < f.length; j++) {
                            var h = f[j];
                            if (typeof h == "object") {
                                if (typeof h.name == "undefined" || h.endTime == "undefined" || h.startTime == "undefined") {
                                    console.log("data format is wrong! propeties should have name or endTime or startTime ");
                                    continue
                                } else {
                                    if (typeof h.endTime != "number" || typeof h.startTime != "number") {
                                        console.log(" endTime or startTime of " + h.name + "Object is not number type");
                                        continue
                                    } else {
                                        d.push(h.name + "_" + (h.endTime - h.startTime));
                                        c = true
                                    }
                                }
                            } else {
                                console.log("data format of Array is wrong! should be single Object");
                                continue
                            }
                        }
                        if (c) {
                            a.timing.sendTimerTracker(d.join("|"));
                            return true
                        }
                    } else {
                        if (typeof f == "object") {
                            if (typeof f.name == "undefined" || f.startTime == "undefined" || f.endTime == "undefined") {
                                console.log("data format is wrong! propeties should be name and startTime ");
                                return false
                            } else {
                                if (typeof f.startTime != "number" || typeof f.endTime != "number") {
                                    console.log(" startTime of " + f.name + "Object is not number type");
                                    return false
                                }
                                a.timing.sendTimerTracker(f.name + "_" + (f.endTime - f.startTime));
                                return true
                            }
                        } else {
                            return false
                        }
                    }
                }
            } catch (g) {
            }
        },sendTimerTracker: function(c) {
            if (c && b.trim(c) != "") {
                recordTrackInfoWithType("2", c)
            }
        },loadBaseTime: function() {
            if (!window.performance) {
                return
            }
            if (typeof stopGlobalTimingLoadFlag == "undefined") {
                a.timing.sendTimerTracker(a.timing.basicTime())
            }
        }}
})(jQuery);
jQuery(window).load(function() {
    setTimeout(function() {
        loli.timing.loadBaseTime()
    }, 3000)
});
(function(a) {
    a(function() {
        if (typeof (pmsInterfaceCallFlag) != "undefined" && pmsInterfaceCallFlag == "0") {
            return
        }
        var c = YHDOBJECT.globalVariable().globalPageCode;
        if (typeof (c) == "undefined" || c == -1) {
            return
        }
        var d = window.loli || (window.loli = {});
        d.app = d.app || {};
        var g = d.app.pms = d.app.pms || {};
        var h = a.cookie("yihaodian_uid") || undefined, i = URLPrefix.pms, b = URLPrefix.statics + "/global/images/blank.gif";
        var k = function() {
            var l = {hasWideScreen: (typeof isWidescreen != "undefined" && isWidescreen == true) ? true : false};
            if (c == "YHD_HOME") {
                l.hasWideScreen = true
            }
            return l
        }, e = function(S) {
            var K = S.infoType, w = S.showTimes, U = S.lTrackerPrefix, B = S.rTrackerPrefix, W = S.buttonTracker, F = S.sourceObject || [], l = S.productList || [], y, Q = [], I = [], x = l.length, C = [], L = "", G = [], M = "", n = 0, J;
            switch (K) {
                case 164:
                    var T = W + "_" + h;
                    y = F.length;
                    M = '<div class="fixed_recommend_btn PMS_message_btn data-tk="' + T + '"><span class="recom_text">您关注的商品到货啦！</span></div>';
                    J = "根据到货商品为您推荐";
                    for (var R = 0; R < y; R++) {
                        var t = F[R], O = t.productId || 0, v = t.cnName, m = t.linkUrl, E = t.picUrl || b, A = t.salePrice || 0, o = t.yhdPrice || 0, q = U + "_" + O + "_" + R, V = typeof (m) == "undefined" ? "" : 'href="' + m + '" data-tk="' + q + '" target="_blank" ';
                        Q.push('<li><dl class="recom_product"><dd class="pic"><a ' + V + 'title="' + v + '"><img src="' + E + '" width="115" height="115" alt="' + v + '" /></a></dd>');
                        Q.push("<dt><a " + V + 'title="' + v + '">' + v + "</a></dt>");
                        Q.push('<dd class="price"><strong>&yen;' + A + "</strong>");
                        if (A != o) {
                            Q.push("<del>&yen;" + o + "</del>")
                        }
                        Q.push('</dd><dd class="btn"><a ' + V + 'class="btn_add_cart">查看详情</a></dd></dl></li>')
                    }
                    Q = Q.join("");
                    I.push('<div class="recom_num_scroll_wrap"><div class="recom_num_scroll"><ul class="clearfix">' + Q + '</ul></div><a class="scroll_arrow arrow_prev" href="javascript:void(0);"></a><a class="scroll_arrow arrow_next');
                    I.push('" href="javascript:void(0);"></a><span class="scroll_num"><em class="cur">1</em>/<em class="total">' + y + "</em></span></div>");
                    I = I.join("");
                    n = 164;
                    break;
                case 165:
                    var t = F[0], P = t.activeId, r = t.deductType || 1, p = t.threshOld || 0, z = t.couponTitle, D = t.couponLinkUrl, u = t.amount || 0, N = t.expiredTime || "0000-00-00", T = W + "_" + h, H, q, V;
                    if (currSiteId == 1) {
                        H = "yhd"
                    } else {
                        if (currSiteId == 2) {
                            H = "yw"
                        }
                    }
                    q = H + "_pmsCoupon_" + P;
                    V = typeof (D) == "undefined" ? "" : 'href="' + D + '" data-tk="' + q + '" target="_blank" ';
                    M = '<div class="fixed_vouchers_btn PMS_message_btn"><a class="recom_text" href="' + D + '" target="_blank" data-blank_tk="' + q + '">您有一张满' + p;
                    J = "除本品外，本券还适用于这些您可能需要的商品";
                    I.push('<div class="recom_vouchers_wrap"><dl><dd class="vouchers_text"><a ' + V + 'title="' + z + '""><span class="meet">满');
                    if (r == 1) {
                        M += "元";
                        I.push('<strong class="yen">&yen;</strong><strong class="price">' + p + " </strong>")
                    } else {
                        if (r == 2) {
                            M += "件";
                            I.push('<strong class="price">' + p + " </strong>件")
                        }
                    }
                    M += "减" + u + "元的抵用券！（将过期）</a></div>";
                    I.push('减</span><span class="reduce"><strong class="yen">&yen;</strong><strong class="price">' + u + "</strong></span></a></dd>");
                    I.push('<dd class="text"><a ' + V + 'title="' + z + '">' + z + "</a></dd>");
                    I.push('<dd class="expiry_date">到期时间：' + N + "</dd></dl></div>");
                    I = I.join("");
                    n = 165;
                    break;
                default:
                    break
            }
            for (var R = 0; R < x; R++) {
                var s = l[R], O = s.productId || 0, v = s.cnName, m = s.linkUrl, E = s.picUrl || b, A = s.salePrice || 0, o = s.yhdPrice || 0, q = B + "_" + O + "_" + R, V = typeof (m) == "undefined" ? "" : 'href="' + m + '" data-tk="' + q + '" target="_blank"';
                C.push('<li><dl class="recom_product"><dd class="pic"><a ' + V + 'title="' + v + '"><img src="' + E + '" width="115" height="115" alt="' + v + '" /></a></dd>');
                C.push("<dt><a " + V + 'title="' + v + '">' + v + "</a></dt>");
                C.push('<dd class="price"><strong>&yen;' + A + "</strong>");
                if (A != o) {
                    C.push("<del>&yen;" + o + "</del>")
                }
                C.push('</dd><dd class="btn"><a ' + V + 'class="btn_add_cart">查看详情</a></dd></dl></li>')
            }
            C = C.join("");
            L = '<div class="recom_screen_scroll_wrap"><div class="recom_screen_scroll"><ul class="clearfix">' + C + '</ul></div><a class="scroll_arrow arrow_prev" href="javascript:void(0);"></a><a class="scroll_arrow arrow_next" href="javascript:void(0);"></a><b class="icon_arrow"></b></div>';
            G.push('<div class="fixed_recommend_main fixed_recommend_main_index wrap" id="globalPMSMessage"><div class="fixed_recommend_wrap"><div class="con"><h3>' + J + '</h3><div class="box clearfix">' + I + L + '</div></div><div class="transparent_bg"></div><span class="close"></span></div>');
            G.push(M + "</div>");
            G = G.join("");
            a("body").append(G);
            if (n > 0) {
                recordTrackInfoWithType("1", "pms_" + n)
            }
            a("#globalPmsMessageBtn").click(function() {
                gotracker(2, a("#globalPmsMessageBtn").attr("data-tk"))
            });
            addTrackerToEvent(a("#globalPMSMessage .con"), "data-tk");
            addTrackerToEvent(a("#globalPMSMessage"), "data-blank_tk");
            j(K, w)
        }, j = function(r, u) {
            var m = k().hasWideScreen, l = a("#globalPMSMessage"), q = l.find(".fixed_recommend_wrap"), v = l.find(".PMS_message_btn"), s = q.find(".close"), w = a.browser.msie && a.browser.version == 6 || false, o = screen.width >= 1280 ? true : false, t = m ? (o ? 5 : 4) : 4, n = false;
            v.animate({bottom: "-1px"}, 300);
            if (w) {
                var x = a(window).height();
                d.delay(window, "scroll", null, function() {
                    var y = a(window).scrollTop();
                    l.css({top: x + y})
                })
            }
            v.click(function() {
                return;
                var y = a(this);
                if (r == 164) {
                    a.getJSON(i + "/pms/infoRemindClearCookie.do?callback=?")
                }
                y.animate({bottom: "-30px"}, 300, function() {
                    q.show().animate({bottom: "0"}, 500);
                    if (!n) {
                        switch (r) {
                            case 164:
                                var z = new p({scrollId: ".recom_num_scroll_wrap",scrollPic: ".recom_num_scroll ul",picItem: "li",visible: "1",speed: 300}), A = new p({scrollId: ".recom_screen_scroll_wrap",scrollPic: ".recom_screen_scroll ul",picItem: "li",visible: t,speed: 600});
                                z.slide();
                                A.slide();
                                break;
                            case 165:
                                var A = new p({scrollId: ".recom_screen_scroll_wrap",scrollPic: ".recom_screen_scroll ul",picItem: "li",visible: t,speed: 600});
                                A.slide();
                                break;
                            default:
                                break
                        }
                        n = true
                    }
                    if (u == 1) {
                        y.remove()
                    }
                })
            });
            s.click(function() {
                q.animate({bottom: "-295px"}, 500, function() {
                    if (u == 0) {
                        q.hide();
                        v.animate({bottom: "-1px"}, 300)
                    } else {
                        q.remove()
                    }
                })
            });
            function p(y) {
                this._config = {scrollId: "",scrollPic: "",picItem: "",visible: "",speed: 400,indexNum: 0};
                this.config = a.extend(this._config, y)
            }
            p.prototype.slide = function() {
                var I = this.config, M = I.visible, N = I.speed, F = a(I.scrollId), A = F.find(I.scrollPic), D = F.find(".scroll_arrow"), y = F.find(".arrow_prev"), B = F.find(".arrow_next"), E = F.find(".scroll_num"), J = E.find(".cur"), L = A.find(I.picItem), O = L.length, z = L.outerWidth(true), H = Math.ceil(O / M), C = 0, G = M * z;
                if (H <= 1) {
                    D.hide();
                    E.hide()
                } else {
                    B.addClass("arrow_next_click")
                }
                F.delegate(".arrow_prev_click", "click", function() {
                    C--;
                    K(C)
                });
                F.delegate(".arrow_next_click", "click", function() {
                    C++;
                    K(C)
                });
                function K(P) {
                    J.text(P + 1);
                    if (P == 0) {
                        y.removeClass("arrow_prev_click");
                        B.addClass("arrow_next_click")
                    } else {
                        if (P == (H - 1)) {
                            y.addClass("arrow_prev_click");
                            B.removeClass("arrow_next_click")
                        } else {
                            y.addClass("arrow_prev_click");
                            B.addClass("arrow_next_click")
                        }
                    }
                    A.animate({marginLeft: -G * P}, N)
                }
            }
        }, f = function() {
            var m = {currSiteId: currSiteId,currSiteType: currSiteType,provinceId: a.cookie("provinceId") || undefined,pageCode: c,productid: a("#productId").val(),merchantId: a("#merchantId").val(),userid: h,searchCategoryId: a("#curCategoryIdToGlobal").val(),searchKeyword: a("#searchword").val(),isLogin: 0}, n = i + "/pms/infoRemind.do?", o = [];
            o.push(n);
            var l = function(p) {
                m.isLogin = p.result;
                for (var q in m) {
                    var r = m[q];
                    if (typeof (r) !== "undefined") {
                        o.push(q + "=" + m[q] + "&")
                    }
                }
                o.push("callback=?");
                n = o.join("");
                a.getJSON(n, function(t) {
                    if (t) {
                        var s = t.value[0];
                        if (s && s && s.status == 1 && s.showFlag == 1) {
                            e(s)
                        }
                    }
                })
            };
            d.globalCheckLogin(l)
        };
        g.message = f;
        setTimeout(function() {
            g.message()
        }, 3000)
    })
})(jQuery);
var YHDREF = YHDREF || {};
(function($) {
    var refParseFunc = null;
    YHDREF.defineGlobalRefParse = function(getRefAttrFunc) {
        refParseFunc = getRefAttrFunc
    };
    $(function() {
        var pageCode = YHDOBJECT.globalVariable().currPageId || 0;
        var head = "gl.", prevTk = "[", afterTk = "]";
        var util = loli.util.url;
        var getPrevPageFlag = function() {
            var _location = location;
            var href = _location.href;
            var params = util.getParams(href);
            if (!params || !params.ref) {
                return 0
            }
            var ref = params.ref;
            if (checkRef(ref)) {
                return ref.substring(ref.lastIndexOf(".") + 1)
            }
            return 0
        };
        var checkRef = function(ref) {
            if (ref.indexOf(head) != 0 || ref.indexOf(prevTk) <= 0 || ref.indexOf(afterTk) <= 0) {
                return false
            }
            var reg = /gl\.\d\.\d\.\w+\.\[[\S]+\]\.[\S]+\.[\S]+$/;
            var result = reg.exec(ref);
            return result ? true : false
        };
        var rewriteUrlByDataRef = function(url, dataRef) {
            var ref = head + currSiteId + "." + 1 + "." + pageCode + "." + prevTk + dataRef[0] + afterTk + "." + prevPageFlag + "." + currentPageFlag;
            var params = {ref: ref};
            return util.appendParams(url, params)
        };
        var rewriteUrlByOriginal = function(url) {
            if (!url || url.indexOf("?") <= 0 || url.indexOf("ref=") <= 0) {
                return url
            }
            var tk = loli.util.url.getParams(url).ref;
            if (typeof (tk) == "undefined" || !tk) {
                return url
            }
            var params = {ref: null};
            return util.appendParams(url, params)
        };
        var prevPageFlag = getPrevPageFlag();
        var currentPageFlag = loli.global.uid;
        var checkDataRef = function(dataRef) {
            return (typeof (dataRef) != "undefined" && (dataRef instanceof Array) && dataRef.length >= 1)
        };
        function isLinkRef(link) {
            return typeof (link) != "undefined" && link && (link.indexOf("http") == 0 || link.indexOf("https") == 0 || link.indexOf("//") == 0)
        }
        $("body").delegate("a, area", "click", function() {
            var _this = $(this);
            var dataRef = _this.data("data-tracker2cookie");
            if (!dataRef) {
                var data_ref = _this.attr("data-ref");
                if (data_ref && data_ref.indexOf("[") == 0 && data_ref.indexOf("]") == data_ref.length - 1) {
                    eval("dataRef = " + data_ref)
                } else {
                    if (data_ref) {
                        data_ref = "['" + data_ref + "']";
                        eval("dataRef = " + data_ref)
                    }
                }
            }
            if (!dataRef && refParseFunc) {
                dataRef = refParseFunc(_this);
                if (checkDataRef(dataRef)) {
                    _this.data("data-tracker2cookie", dataRef)
                }
            }
            var link = jQuery.trim(_this.attr("href"));
            var spmData = loli.spm.getData(_this);
            if (isLinkRef(link)) {
                if (checkDataRef(dataRef)) {
                    addTrackPositionToCookie.apply(window, [1].concat(dataRef))
                } else {
                    if (jQuery.trim(dataRef) != "") {
                        addTrackPositionToCookie(1, dataRef)
                    }
                }
                var _rewrite = _this.data("data-globalRewrite");
                if (_rewrite && _rewrite == 1) {
                    return
                }
                if (checkDataRef(dataRef)) {
                } else {
                    var flag = link.indexOf("#");
                    if (flag > -1) {
                        var url = link.substring(0, flag);
                        var end = link.substring(flag);
                        link = rewriteUrlByOriginal(url) + end
                    } else {
                        link = rewriteUrlByOriginal(link)
                    }
                }
                if (spmData) {
                    var tc = spmData.tc;
                    var tp = spmData.tp;
                    var params = {tc: tc,tp: tp};
                    link = util.appendParams(link, params)
                }
                _this.attr("href", link);
                _this.data("data-globalRewrite", 1)
            } else {
                var isTrkCustom = jQuery.trim(_this.attr("isTrkCustom"));
                if (typeof (isTrkCustom) != "undefined" && isTrkCustom && isTrkCustom == "1") {
                    return
                } else {
                    if (checkDataRef(dataRef)) {
                        var pmId = dataRef[2] ? dataRef[2] : 2;
                        var tk = dataRef[0];
                        var productId = dataRef[1] ? dataRef[1] : null;
                        gotracker(pmId, tk, productId, spmData)
                    } else {
                        if (spmData) {
                            gotracker(null, null, null, spmData)
                        }
                    }
                }
            }
        })
    })
})(jQuery);
(function(d) {
    var f = {urlMap: [],resultMap: [],loadedCount: 0,config: {},cdnConfig: function(b) {
            f.config = b;
            var a = f.config.random;
            if (a) {
                var c = Math.floor(Math.random() * 100 + 1);
                if (c <= a) {
                    f.config.canDetection = true
                }
            }
        },canDetection: function() {
            var a = window.navigator.userAgent.indexOf("Chrome") !== -1;
            if (a && window.performance && f.config.canDetection) {
                return true
            }
            return false
        },cdnAddObject: function(a, b) {
            if (!f.canDetection()) {
                return
            }
            f.urlMap.push({key: a,url: b + "?r=" + Math.random()})
        },cdnDetection: function(i) {
            if (!f.canDetection()) {
                return
            }
            var b = f.urlMap, j = b.length;
            for (var c = 0; c < j; c++) {
                var a = b[c];
                this.loadResource(a)
            }
        },loaded: function() {
            var b = f;
            if (b.urlMap.length == b.loadedCount) {
                var a = b.config.callback;
                a();
                return
            }
        },loadResource: function(a) {
            var b = new Image();
            b.onload = function() {
                try {
                    var h = window.performance.getEntriesByName(a.url);
                    if (h == null || h.length < 1) {
                        return
                    }
                    f.loadedCount++;
                    a.costTime = Math.round(h[0].responseEnd - h[0].startTime);
                    f.resultMap.push(a);
                    f.loaded()
                } catch (c) {
                }
            };
            b.src = a.url
        }};
    var e = window.loli || (window.loli = {});
    e.cdnDetection = f;
    jQuery(document).ready(function() {
        var a = d("body").attr("data-cdnDetection");
        if (a == "-1" || a == null) {
            return
        }
        a = jQuery.parseJSON(a);
        if (!a.random || !a.child) {
            return
        }
        var i = a.child, c = i.length;
        if (c < 1) {
            return
        }
        var b = e.cdnDetection;
        b.cdnConfig({random: a.random,callback: function() {
                var h = b.resultMap, B = "http://opsdev.yhd.com/trace/?time=" + new Date().getTime();
                var z = "d=";
                var t = h.length;
                for (var u = 0; u < t; u++) {
                    var v = h[u];
                    var x = v.key;
                    var y = "0.0.0.0";
                    var g = 0;
                    var A = v.costTime;
                    z += x + "," + y + "," + g + "," + A;
                    if (u < t - 1) {
                        z = z + ";"
                    }
                }
                var w = new Image();
                w.src = B + "&" + z
            }});
        setTimeout(function() {
            for (var h = 0; h < c; h++) {
                var g = i[h];
                b.cdnAddObject(g.key, g.url)
            }
            b.cdnDetection()
        }, 10000)
    })
})(jQuery);
var glaCookieHandler = {};
(function(s) {
    var o = function(c) {
        var a = document.cookie;
        var e = a.split("; ");
        for (var b = 0; b < e.length; b++) {
            var d = e[b].split("=");
            if (d[0] == c) {
                return d[1]
            }
        }
        return null
    };
    ilog("##gla-----------");
    var t = "gla";
    var s = s || {}, r = o("provinceId"), p = o(t);
    var u = {p_1: "-10",p_2: "-20",p_3: "-30",p_4: "25",p_5: "37",p_6: "50",p_7: "-40",p_8: "62",p_9: "75",p_10: "88",p_11: "97",p_12: "111",p_13: "133",p_14: "150",p_15: "159",p_16: "170",p_17: "187",p_18: "205",p_19: "222",p_20: "237",p_21: "258",p_22: "274",p_23: "294",p_24: "303",p_25: "320",p_26: "327",p_27: "337",p_28: "351",p_29: "359",p_30: "377",p_32: "387"};
    function n() {
        var a = z();
        if (a && a.provinceId) {
            return a.provinceId
        } else {
            return r
        }
    }
    function v() {
        var a = z();
        if (a && a.cityId) {
            return a.provinceId
        }
        return null
    }
    function q() {
        var b = false;
        var a = z();
        if (r && a && a.provinceId && a.provinceId == r) {
            b = true
        }
        return b
    }
    function z() {
        if (!p) {
            return null
        }
        var b = {};
        var a = p.split("_");
        var c = a[0].split(".");
        if (c.length < 2) {
            return null
        }
        b.provinceId = c[0];
        b.cityId = c[1];
        b.hasUnionSite = false;
        if (a.length > 1 && a[1] != "0") {
            b.hasUnionSite = true;
            b.unionSiteDomain = a[1]
        }
        b.willingToUnionSite = 1;
        if (a.length > 2 && a[2] == "0") {
            b.willingToUnionSite = 0
        }
        return b
    }
    function w(b) {
        if (!b || !b.provinceId) {
            return
        }
        if (!b.cityId) {
            b.cityId = u["p_" + b.provinceId]
        }
        var c = [];
        c.push(b.provinceId + "." + b.cityId);
        if (b.unionSiteDomain) {
            c.push(b.unionSiteDomain);
            if (b.willingToUnionSite && b.willingToUnionSite != "0") {
                c.push(1)
            } else {
                c.push(0)
            }
        } else {
            c.push(0)
        }
        var a = new Date();
        a.setTime(new Date().getTime() + 800 * 24 * 60 * 60 * 1000);
        document.cookie = t + "=" + c.join("_") + ";path=/;domain=." + no3wUrl + ";expires=" + a.toGMTString()
    }
    function y(b) {
        if (!b || !b.provinceId) {
            return
        }
        w(b);
        var a = new Date();
        a.setTime(new Date().getTime() + 800 * 24 * 60 * 60 * 1000);
        document.cookie = "provinceId=" + b.provinceId + ";path=/;domain=." + no3wUrl + ";expires=" + a.toGMTString()
    }
    function x() {
        var b = "";
        if (q()) {
            var a = z();
            if (a && a.unionSiteDomain && a.willingToUnionSite) {
                b = a.unionSiteDomain
            }
        }
        return b
    }
    s.glaCookieKey = t;
    s.defaultCityObj = u;
    s.analysisGla = z;
    s.genGlaCookie = w;
    s.gotoUnionSite = x;
    s.getCookie = o;
    s.check2ProvinceIsSame = q;
    s.resetGlaAndProvinceCookie = y;
    s.getProvinceId = n
})(glaCookieHandler);
(function(b) {
    var c = ["wheel", "mousewheel", "DOMMouseScroll", "MozMousePixelScroll"], j = ("onwheel" in document || document.documentMode >= 9) ? ["wheel"] : ["mousewheel", "DomMouseScroll", "MozMousePixelScroll"], g = Array.prototype.slice, h, a;
    if (b.event.fixHooks) {
        for (var d = c.length; d; ) {
            b.event.fixHooks[c[--d]] = b.event.mouseHooks
        }
    }
    var e = b.event.special.mousewheel = {version: "3.1.9",setup: function() {
            if (this.addEventListener) {
                for (var i = j.length; i; ) {
                    this.addEventListener(j[--i], k, false)
                }
            } else {
                this.onmousewheel = k
            }
            b.data(this, "mousewheel-line-height", e.getLineHeight(this));
            b.data(this, "mousewheel-page-height", e.getPageHeight(this))
        },teardown: function() {
            if (this.removeEventListener) {
                for (var i = j.length; i; ) {
                    this.removeEventListener(j[--i], k, false)
                }
            } else {
                this.onmousewheel = null
            }
        },getLineHeight: function(i) {
            return parseInt(b(i)["offsetParent" in b.fn ? "offsetParent" : "parent"]().css("fontSize"), 10)
        },getPageHeight: function(i) {
            return b(i).height()
        },settings: {adjustOldDeltas: true}};
    b.fn.extend({mousewheel: function(i) {
            return i ? this.bind("mousewheel", i) : this.trigger("mousewheel")
        },unmousewheel: function(i) {
            return this.unbind("mousewheel", i)
        }});
    function k(r) {
        var t = r || window.event, o = g.call(arguments, 1), q = 0, m = 0, i = 0, n = 0;
        r = b.event.fix(t);
        r.type = "mousewheel";
        if ("detail" in t) {
            i = t.detail * -1
        }
        if ("wheelDelta" in t) {
            i = t.wheelDelta
        }
        if ("wheelDeltaY" in t) {
            i = t.wheelDeltaY
        }
        if ("wheelDeltaX" in t) {
            m = t.wheelDeltaX * -1
        }
        if ("axis" in t && t.axis === t.HORIZONTAL_AXIS) {
            m = i * -1;
            i = 0
        }
        q = i === 0 ? m : i;
        if ("deltaY" in t) {
            i = t.deltaY * -1;
            q = i
        }
        if ("deltaX" in t) {
            m = t.deltaX;
            if (i === 0) {
                q = m * -1
            }
        }
        if (i === 0 && m === 0) {
            return
        }
        if (t.deltaMode === 1) {
            var p = b.data(this, "mousewheel-line-height");
            q *= p;
            i *= p;
            m *= p
        } else {
            if (t.deltaMode === 2) {
                var s = b.data(this, "mousewheel-page-height");
                q *= s;
                i *= s;
                m *= s
            }
        }
        n = Math.max(Math.abs(i), Math.abs(m));
        if (!a || n < a) {
            a = n;
            if (l(t, n)) {
                a /= 40
            }
        }
        if (l(t, n)) {
            q /= 40;
            m /= 40;
            i /= 40
        }
        q = Math[q >= 1 ? "floor" : "ceil"](q / a);
        m = Math[m >= 1 ? "floor" : "ceil"](m / a);
        i = Math[i >= 1 ? "floor" : "ceil"](i / a);
        r.deltaX = m;
        r.deltaY = i;
        r.deltaFactor = a;
        r.deltaMode = 0;
        o.unshift(r, q, m, i);
        if (h) {
            clearTimeout(h)
        }
        h = setTimeout(f, 200);
        return (b.event.dispatch || b.event.handle).apply(this, o)
    }
    function f() {
        a = null
    }
    function l(m, i) {
        return e.settings.adjustOldDeltas && m.type === "mousewheel" && i % 120 === 0
    }
})(jQuery);
!function(e, d, f) {
    e.fn.jScrollPane = function(b) {
        function a(be, a7) {
            function aU(j) {
                var i, h, g, o, n, m, l = !1, k = !1;
                if (bl = j, aE === f) {
                    n = be.scrollTop(), m = be.scrollLeft(), be.css({overflow: "hidden",padding: 0}), aD = be.innerWidth() + a0, aC = be.innerHeight(), be.width(aD), aE = e('<div class="jspPane" />').css("padding", a8).append(be.children()), aB = e('<div class="jspContainer" />').css({width: aD + "px",height: aC + "px"}).append(aE).appendTo(be)
                } else {
                    if (be.css("width", ""), l = bl.stickToBottom && aQ(), k = bl.stickToRight && aN(), o = be.innerWidth() + a0 != aD || be.outerHeight() != aC, o && (aD = be.innerWidth() + a0, aC = be.innerHeight(), aB.css({width: aD + "px",height: aC + "px"})), !o && c == aA && aE.outerHeight() == bA) {
                        return be.width(aD), void 0
                    }
                    c = aA, aE.css("width", ""), be.width(aD), aB.find(">.jspVerticalBar,>.jspHorizontalBar").remove().end()
                }
                aE.css("overflow", "auto"), aA = j.contentWidth ? j.contentWidth : aE[0].scrollWidth, bA = aE[0].scrollHeight, aE.css("overflow", ""), bz = aA / aD, bv = bA / aC, bm = bv > 1, bU = bz > 1, bU || bm ? (be.addClass("jspScrollable"), i = bl.maintainPosition && (a2 || bp), i && (h = aW(), g = aS()), bS(), bM(), bF(), i && (aV(k ? aA - aD : h, !1), bq(l ? bA - aC : g, !1)), aK(), aL(), bQ(), bl.enableKeyboardNavigation && bK(), bl.clickOnTrack && bL(), bt(), bl.hijackInternalLinks && bk()) : (be.removeClass("jspScrollable"), aE.css({top: 0,left: 0,width: aB.width() - a0}), bB(), aI(), aP(), bI()), bl.autoReinitialise && !bi ? bi = setInterval(function() {
                    aU(bl)
                }, bl.autoReinitialiseDelay) : !bl.autoReinitialise && bi && clearInterval(bi), n && be.scrollTop(0) && bq(n, !1), m && be.scrollLeft(0) && aV(m, !1), be.trigger("jsp-initialised", [bU || bm])
            }
            function bS() {
                bm && (aB.append(e('<div class="jspVerticalBar" />').append(e('<div class="jspCap jspCapTop" />'), e('<div class="jspTrack" />').append(e('<div class="jspDrag" />').append(e('<div class="jspDragTop" />'), e('<div class="jspDragBottom" />'))), e('<div class="jspCap jspCapBottom" />'))), bV = aB.find(">.jspVerticalBar"), bf = bV.find(">.jspTrack"), a6 = bf.find(">.jspDrag"), bl.showArrows && (br = e('<a class="jspArrow jspArrowUp" />').bind("mousedown.jsp", bR(0, -1)).bind("click.jsp", aM), bo = e('<a class="jspArrow jspArrowDown" />').bind("mousedown.jsp", bR(0, 1)).bind("click.jsp", aM), bl.arrowScrollOnHover && (br.bind("mouseover.jsp", bR(0, -1, br)), bo.bind("mouseover.jsp", bR(0, 1, bo))), aJ(bf, bl.verticalArrowPositions, br, bo)), aR = aC, aB.find(">.jspVerticalBar>.jspCap:visible,>.jspVerticalBar>.jspArrow").each(function() {
                    aR -= e(this).outerHeight()
                }), a6.hover(function() {
                    a6.addClass("jspHover")
                }, function() {
                    a6.removeClass("jspHover")
                }).bind("mousedown.jsp", function(g) {
                    e("html").bind("dragstart.jsp selectstart.jsp", aM), a6.addClass("jspActive");
                    var h = g.pageY - a6.position().top;
                    return e("html").bind("mousemove.jsp", function(i) {
                        bE(i.pageY - h, !1)
                    }).bind("mouseup.jsp mouseleave.jsp", bG), !1
                }), bO())
            }
            function bO() {
                bf.height(aR + "px"), a2 = 0, a3 = bl.verticalGutter + bf.outerWidth(), aE.width(aD - a3 - a0);
                try {
                    0 === bV.position().left && aE.css("margin-left", a3 + "px")
                } catch (g) {
                }
            }
            function bM() {
                bU && (aB.append(e('<div class="jspHorizontalBar" />').append(e('<div class="jspCap jspCapLeft" />'), e('<div class="jspTrack" />').append(e('<div class="jspDrag" />').append(e('<div class="jspDragLeft" />'), e('<div class="jspDragRight" />'))), e('<div class="jspCap jspCapRight" />'))), bh = aB.find(">.jspHorizontalBar"), a5 = bh.find(">.jspTrack"), aH = a5.find(">.jspDrag"), bl.showArrows && (aF = e('<a class="jspArrow jspArrowLeft" />').bind("mousedown.jsp", bR(-1, 0)).bind("click.jsp", aM), bx = e('<a class="jspArrow jspArrowRight" />').bind("mousedown.jsp", bR(1, 0)).bind("click.jsp", aM), bl.arrowScrollOnHover && (aF.bind("mouseover.jsp", bR(-1, 0, aF)), bx.bind("mouseover.jsp", bR(1, 0, bx))), aJ(a5, bl.horizontalArrowPositions, aF, bx)), aH.hover(function() {
                    aH.addClass("jspHover")
                }, function() {
                    aH.removeClass("jspHover")
                }).bind("mousedown.jsp", function(g) {
                    e("html").bind("dragstart.jsp selectstart.jsp", aM), aH.addClass("jspActive");
                    var h = g.pageX - aH.position().left;
                    return e("html").bind("mousemove.jsp", function(i) {
                        by(i.pageX - h, !1)
                    }).bind("mouseup.jsp mouseleave.jsp", bG), !1
                }), bD = aB.innerWidth(), bH())
            }
            function bH() {
                aB.find(">.jspHorizontalBar>.jspCap:visible,>.jspHorizontalBar>.jspArrow").each(function() {
                    bD -= e(this).outerWidth()
                }), a5.width(bD + "px"), bp = 0
            }
            function bF() {
                if (bU && bm) {
                    var g = a5.outerHeight(), h = bf.outerWidth();
                    aR -= g, e(bh).find(">.jspCap:visible,>.jspArrow").each(function() {
                        bD += e(this).outerWidth()
                    }), bD -= h, aC -= h, aD -= g, a5.parent().append(e('<div class="jspCorner" />').css("width", g + "px")), bO(), bH()
                }
                bU && aE.width(aB.outerWidth() - a0 + "px"), bA = aE.outerHeight(), bv = bA / aC, bU && (bd = Math.ceil(1 / bz * bD), bd > bl.horizontalDragMaxWidth ? bd = bl.horizontalDragMaxWidth : bd < bl.horizontalDragMinWidth && (bd = bl.horizontalDragMinWidth), aH.width(bd + "px"), bN = bD - bd, bw(bp)), bm && (bJ = Math.ceil(1 / bv * aR), bJ > bl.verticalDragMaxHeight ? bJ = bl.verticalDragMaxHeight : bJ < bl.verticalDragMinHeight && (bJ = bl.verticalDragMinHeight), a6.height(bJ + "px"), bT = aR - bJ, bC(a2))
            }
            function aJ(i, h, g, m) {
                var l, k = "before", j = "after";
                "os" == h && (h = /Mac/.test(navigator.platform) ? "after" : "split"), h == k ? j = h : h == j && (k = h, l = g, g = m, m = l), i[k](g)[j](m)
            }
            function bR(h, g, i) {
                return function() {
                    return bP(h, g, this, i), this.blur(), !1
                }
            }
            function bP(m, l, k, j) {
                k = e(k).addClass("jspActive");
                var i, h, g = !0, n = function() {
                    0 !== m && aG.scrollByX(m * bl.arrowButtonSpeed), 0 !== l && aG.scrollByY(l * bl.arrowButtonSpeed), h = setTimeout(n, g ? bl.initialDelay : bl.arrowRepeatFreq), g = !1
                };
                n(), i = j ? "mouseout.jsp" : "mouseup.jsp", j = j || e("html"), j.bind(i, function() {
                    k.removeClass("jspActive"), h && clearTimeout(h), h = null, j.unbind(i)
                })
            }
            function bL() {
                bI(), bm && bf.bind("mousedown.jsp", function(n) {
                    if (n.originalTarget === f || n.originalTarget == n.currentTarget) {
                        var m, l = e(this), k = l.offset(), j = n.pageY - k.top - a2, i = !0, h = function() {
                            var o = l.offset(), r = n.pageY - o.top - bJ / 2, q = aC * bl.scrollPagePercent, p = bT * q / (bA - aC);
                            if (0 > j) {
                                a2 - p > r ? aG.scrollByY(-q) : bE(r)
                            } else {
                                if (!(j > 0)) {
                                    return g(), void 0
                                }
                                r > a2 + p ? aG.scrollByY(q) : bE(r)
                            }
                            m = setTimeout(h, i ? bl.initialDelay : bl.trackClickRepeatFreq), i = !1
                        }, g = function() {
                            m && clearTimeout(m), m = null, e(document).unbind("mouseup.jsp", g)
                        };
                        return h(), e(document).bind("mouseup.jsp", g), !1
                    }
                }), bU && a5.bind("mousedown.jsp", function(n) {
                    if (n.originalTarget === f || n.originalTarget == n.currentTarget) {
                        var m, l = e(this), k = l.offset(), j = n.pageX - k.left - bp, i = !0, h = function() {
                            var o = l.offset(), r = n.pageX - o.left - bd / 2, q = aD * bl.scrollPagePercent, p = bN * q / (aA - aD);
                            if (0 > j) {
                                bp - p > r ? aG.scrollByX(-q) : by(r)
                            } else {
                                if (!(j > 0)) {
                                    return g(), void 0
                                }
                                r > bp + p ? aG.scrollByX(q) : by(r)
                            }
                            m = setTimeout(h, i ? bl.initialDelay : bl.trackClickRepeatFreq), i = !1
                        }, g = function() {
                            m && clearTimeout(m), m = null, e(document).unbind("mouseup.jsp", g)
                        };
                        return h(), e(document).bind("mouseup.jsp", g), !1
                    }
                })
            }
            function bI() {
                a5 && a5.unbind("mousedown.jsp"), bf && bf.unbind("mousedown.jsp")
            }
            function bG() {
                e("html").unbind("dragstart.jsp selectstart.jsp mousemove.jsp mouseup.jsp mouseleave.jsp"), a6 && a6.removeClass("jspActive"), aH && aH.removeClass("jspActive")
            }
            function bE(h, g) {
                bm && (0 > h ? h = 0 : h > bT && (h = bT), g === f && (g = bl.animateScroll), g ? aG.animate(a6, "top", h, bC) : (a6.css("top", h), bC(h)))
            }
            function bC(h) {
                h === f && (h = a6.position().top), aB.scrollTop(0), a2 = h;
                var g = 0 === a2, k = a2 == bT, j = h / bT, i = -j * (bA - aC);
                (aO != g || a9 != k) && (aO = g, a9 = k, be.trigger("jsp-arrow-change", [aO, a9, bj, a1])), bu(g, k), aE.css("top", i), be.trigger("jsp-scroll-y", [-i, g, k]).trigger("scroll")
            }
            function by(h, g) {
                bU && (0 > h ? h = 0 : h > bN && (h = bN), g === f && (g = bl.animateScroll), g ? aG.animate(aH, "left", h, bw) : (aH.css("left", h), bw(h)))
            }
            function bw(h) {
                h === f && (h = aH.position().left), aB.scrollTop(0), bp = h;
                var g = 0 === bp, k = bp == bN, j = h / bN, i = -j * (aA - aD);
                (bj != g || a1 != k) && (bj = g, a1 = k, be.trigger("jsp-arrow-change", [aO, a9, bj, a1])), bs(g, k), aE.css("left", i), be.trigger("jsp-scroll-x", [-i, g, k]).trigger("scroll")
            }
            function bu(h, g) {
                bl.showArrows && (br[h ? "addClass" : "removeClass"]("jspDisabled"), bo[g ? "addClass" : "removeClass"]("jspDisabled"))
            }
            function bs(h, g) {
                bl.showArrows && (aF[h ? "addClass" : "removeClass"]("jspDisabled"), bx[g ? "addClass" : "removeClass"]("jspDisabled"))
            }
            function bq(h, g) {
                var i = h / (bA - aC);
                bE(i * bT, g)
            }
            function aV(h, g) {
                var i = h / (aA - aD);
                by(i * bN, g)
            }
            function aT(m, k, i) {
                var h, g, u, t, s, r, q, p, o, n = 0, l = 0;
                try {
                    h = e(m)
                } catch (j) {
                    return
                }
                for (g = h.outerHeight(), u = h.outerWidth(), aB.scrollTop(0), aB.scrollLeft(0); !h.is(".jspPane"); ) {
                    if (n += h.position().top, l += h.position().left, h = h.offsetParent(), /^body|html$/i.test(h[0].nodeName)) {
                        return
                    }
                }
                t = aS(), r = t + aC, t > n || k ? p = n - bl.horizontalGutter : n + g > r && (p = n - aC + g + bl.horizontalGutter), isNaN(p) || bq(p, i), s = aW(), q = s + aD, s > l || k ? o = l - bl.horizontalGutter : l + u > q && (o = l - aD + u + bl.horizontalGutter), isNaN(o) || aV(o, i)
            }
            function aW() {
                return -aE.position().left
            }
            function aS() {
                return -aE.position().top
            }
            function aQ() {
                var g = bA - aC;
                return g > 20 && g - aS() < 10
            }
            function aN() {
                var g = aA - aD;
                return g > 20 && g - aW() < 10
            }
            function aL() {
                aB.unbind(a4).bind(a4, function(i, h, g, m) {
                    var l = bp, k = a2, j = i.deltaFactor || bl.mouseWheelSpeed;
                    return aG.scrollBy(g * j, -m * j, !1), l == bp && k == a2
                })
            }
            function bB() {
                aB.unbind(a4)
            }
            function aM() {
                return !1
            }
            function aK() {
                aE.find(":input,a").unbind("focus.jsp").bind("focus.jsp", function(g) {
                    aT(g.target, !1)
                })
            }
            function aI() {
                aE.find(":input,a").unbind("focus.jsp")
            }
            function bK() {
                function g() {
                    var l = bp, k = a2;
                    switch (j) {
                        case 40:
                            aG.scrollByY(bl.keyboardSpeed, !1);
                            break;
                        case 38:
                            aG.scrollByY(-bl.keyboardSpeed, !1);
                            break;
                        case 34:
                        case 32:
                            aG.scrollByY(aC * bl.scrollPagePercent, !1);
                            break;
                        case 33:
                            aG.scrollByY(-aC * bl.scrollPagePercent, !1);
                            break;
                        case 39:
                            aG.scrollByX(bl.keyboardSpeed, !1);
                            break;
                        case 37:
                            aG.scrollByX(-bl.keyboardSpeed, !1)
                    }
                    return i = l != bp || k != a2
                }
                var j, i, h = [];
                bU && h.push(bh[0]), bm && h.push(bV[0]), aE.focus(function() {
                    be.focus()
                }), be.attr("tabindex", 0).unbind("keydown.jsp keypress.jsp").bind("keydown.jsp", function(m) {
                    if (m.target === this || h.length && e(m.target).closest(h).length) {
                        var l = bp, k = a2;
                        switch (m.keyCode) {
                            case 40:
                            case 38:
                            case 34:
                            case 32:
                            case 33:
                            case 39:
                            case 37:
                                j = m.keyCode, g();
                                break;
                            case 35:
                                bq(bA - aC), j = null;
                                break;
                            case 36:
                                bq(0), j = null
                        }
                        return i = m.keyCode == j && l != bp || k != a2, !i
                    }
                }).bind("keypress.jsp", function(k) {
                    return k.keyCode == j && g(), !i
                }), bl.hideFocus ? (be.css("outline", "none"), "hideFocus" in aB[0] && be.attr("hideFocus", !0)) : (be.css("outline", ""), "hideFocus" in aB[0] && be.attr("hideFocus", !1))
            }
            function aP() {
                be.attr("tabindex", "-1").removeAttr("tabindex").unbind("keydown.jsp keypress.jsp")
            }
            function bt() {
                if (location.hash && location.hash.length > 1) {
                    var g, j, i = escape(location.hash.substr(1));
                    try {
                        g = e("#" + i + ', a[name="' + i + '"]')
                    } catch (h) {
                        return
                    }
                    g.length && aE.find(i) && (0 === aB.scrollTop() ? j = setInterval(function() {
                        aB.scrollTop() > 0 && (aT(g, !0), e(document).scrollTop(aB.position().top), clearInterval(j))
                    }, 50) : (aT(g, !0), e(document).scrollTop(aB.position().top)))
                }
            }
            function bk() {
                e(document.body).data("jspHijack") || (e(document.body).data("jspHijack", !0), e(document.body).delegate("a[href*=#]", "click", function(j) {
                    var i, h, g, p, o, n, m = this.href.substr(0, this.href.indexOf("#")), l = location.href;
                    if (-1 !== location.href.indexOf("#") && (l = location.href.substr(0, location.href.indexOf("#"))), m === l) {
                        i = escape(this.href.substr(this.href.indexOf("#") + 1));
                        try {
                            h = e("#" + i + ', a[name="' + i + '"]')
                        } catch (k) {
                            return
                        }
                        h.length && (g = h.closest(".jspScrollable"), p = g.data("jsp"), p.scrollToElement(h, !0), g[0].scrollIntoView && (o = e(d).scrollTop(), n = h.offset().top, (o > n || n > o + e(d).height()) && g[0].scrollIntoView()), j.preventDefault())
                    }
                }))
            }
            function bQ() {
                var h, g, l, k, j, i = !1;
                aB.unbind("touchstart.jsp touchmove.jsp touchend.jsp click.jsp-touchclick").bind("touchstart.jsp", function(m) {
                    var n = m.originalEvent.touches[0];
                    h = aW(), g = aS(), l = n.pageX, k = n.pageY, j = !1, i = !0
                }).bind("touchmove.jsp", function(o) {
                    if (i) {
                        var n = o.originalEvent.touches[0], m = bp, p = a2;
                        return aG.scrollTo(h + l - n.pageX, g + k - n.pageY), j = j || Math.abs(l - n.pageX) > 5 || Math.abs(k - n.pageY) > 5, m == bp && p == a2
                    }
                }).bind("touchend.jsp", function() {
                    i = !1
                }).bind("click.jsp-touchclick", function() {
                    return j ? (j = !1, !1) : void 0
                })
            }
            function bn() {
                var h = aS(), g = aW();
                be.removeClass("jspScrollable").unbind(".jsp"), be.replaceWith(bg.append(aE.children())), bg.scrollTop(h), bg.scrollLeft(g), bi && clearInterval(bi)
            }
            var bl, aE, aD, aC, aB, aA, bA, bz, bv, bm, bU, a6, bT, a2, aH, bN, bp, bV, bf, a3, aR, bJ, br, bo, bh, a5, bD, bd, aF, bx, bi, a8, a0, c, aG = this, aO = !0, bj = !0, a9 = !1, a1 = !1, bg = be.clone(!1, !1).empty(), a4 = e.fn.mwheelIntent ? "mwheelIntent.jsp" : "mousewheel.jsp";
            "border-box" === be.css("box-sizing") ? (a8 = 0, a0 = 0) : (a8 = be.css("paddingTop") + " " + be.css("paddingRight") + " " + be.css("paddingBottom") + " " + be.css("paddingLeft"), a0 = (parseInt(be.css("paddingLeft"), 10) || 0) + (parseInt(be.css("paddingRight"), 10) || 0)), e.extend(aG, {reinitialise: function(g) {
                    g = e.extend({}, bl, g), aU(g)
                },scrollToElement: function(h, g, i) {
                    aT(h, g, i)
                },scrollTo: function(h, g, i) {
                    aV(h, i), bq(g, i)
                },scrollToX: function(h, g) {
                    aV(h, g)
                },scrollToY: function(h, g) {
                    bq(h, g)
                },scrollToPercentX: function(h, g) {
                    aV(h * (aA - aD), g)
                },scrollToPercentY: function(h, g) {
                    bq(h * (bA - aC), g)
                },scrollBy: function(h, g, i) {
                    aG.scrollByX(h, i), aG.scrollByY(g, i)
                },scrollByX: function(h, g) {
                    var j = aW() + Math[0 > h ? "floor" : "ceil"](h), i = j / (aA - aD);
                    by(i * bN, g)
                },scrollByY: function(h, g) {
                    var j = aS() + Math[0 > h ? "floor" : "ceil"](h), i = j / (bA - aC);
                    bE(i * bT, g)
                },positionDragX: function(h, g) {
                    by(h, g)
                },positionDragY: function(h, g) {
                    bE(h, g)
                },animate: function(h, g, k, j) {
                    var i = {};
                    i[g] = k, h.animate(i, {duration: bl.animateDuration,easing: bl.animateEase,queue: !1,step: j})
                },getContentPositionX: function() {
                    return aW()
                },getContentPositionY: function() {
                    return aS()
                },getContentWidth: function() {
                    return aA
                },getContentHeight: function() {
                    return bA
                },getPercentScrolledX: function() {
                    return aW() / (aA - aD)
                },getPercentScrolledY: function() {
                    return aS() / (bA - aC)
                },getIsScrollableH: function() {
                    return bU
                },getIsScrollableV: function() {
                    return bm
                },getContentPane: function() {
                    return aE
                },scrollToBottom: function(g) {
                    bE(bT, g)
                },hijackInternalLinks: e.noop,destroy: function() {
                    bn()
                }}), aU(a7)
        }
        return b = e.extend({}, e.fn.jScrollPane.defaults, b), e.each(["arrowButtonSpeed", "trackClickSpeed", "keyboardSpeed"], function() {
            b[this] = b[this] || b.speed
        }), this.each(function() {
            var c = e(this), g = c.data("jsp");
            g ? g.reinitialise(b) : (e("script", c).filter('[type="text/javascript"],:not([type])').remove(), g = new a(c, b), c.data("jsp", g))
        })
    }, e.fn.jScrollPane.defaults = {showArrows: !1,maintainPosition: !0,stickToBottom: !1,stickToRight: !1,clickOnTrack: !0,autoReinitialise: !1,autoReinitialiseDelay: 500,verticalDragMinHeight: 0,verticalDragMaxHeight: 99999,horizontalDragMinWidth: 0,horizontalDragMaxWidth: 99999,contentWidth: f,animateScroll: !1,animateDuration: 300,animateEase: "linear",hijackInternalLinks: !1,verticalGutter: 4,horizontalGutter: 4,mouseWheelSpeed: 3,arrowButtonSpeed: 0,arrowRepeatFreq: 50,arrowScrollOnHover: !1,trackClickSpeed: 0,trackClickRepeatFreq: 70,verticalArrowPositions: "split",horizontalArrowPositions: "split",enableKeyboardNavigation: !0,hideFocus: !1,keyboardSpeed: 0,initialDelay: 300,speed: 30,scrollPagePercent: 0.8}
}(jQuery, this);
var funParabola = function(L, c, y) {
    var H = {speed: 166.67,curvature: 0.0007,progress: function() {
        },complete: function() {
        }};
    var G = {};
    y = y || {};
    for (var a in H) {
        G[a] = y[a] || H[a]
    }
    var b = {mark: function() {
            return this
        },position: function() {
            return this
        },move: function() {
            return this
        },init: function() {
            return this
        }};
    var I = "margin", x = document.createElement("div");
    if ("oninput" in x) {
        ["", "ms", "webkit"].forEach(function(d) {
            var e = d + (d ? "T" : "t") + "ransform";
            if (e in x.style) {
                I = e
            }
        })
    }
    var J = G.curvature, A = 0, z = 0;
    var D = true;
    if (L && c && L.nodeType == 1 && c.nodeType == 1) {
        var B = {}, F = {};
        var w = {}, E = {};
        var C = {}, K = {};
        b.mark = function() {
            if (D == false) {
                return this
            }
            if (typeof C.x == "undefined") {
                this.position()
            }
            L.setAttribute("data-center", [C.x, C.y].join());
            c.setAttribute("data-center", [K.x, K.y].join());
            return this
        };
        b.position = function() {
            if (D == false) {
                return this
            }
            var d = document.documentElement.scrollLeft || document.body.scrollLeft, e = document.documentElement.scrollTop || document.body.scrollTop;
            if (I == "margin") {
                L.style.marginLeft = L.style.marginTop = "0px"
            } else {
                L.style[I] = "translate(0, 0)"
            }
            B = L.getBoundingClientRect();
            F = c.getBoundingClientRect();
            w = {x: B.left + (B.right - B.left) / 2 + d,y: B.top + (B.bottom - B.top) / 2 + e};
            E = {x: F.left + (F.right - F.left) / 2 + d,y: F.top + (F.bottom - F.top) / 2 + e};
            C = {x: 0,y: 0};
            K = {x: -1 * (w.x - E.x),y: -1 * (w.y - E.y)};
            A = (K.y - J * K.x * K.x) / K.x;
            return this
        };
        b.move = function() {
            if (D == false) {
                return this
            }
            var e = 0, d = K.x > 0 ? 1 : -1;
            var f = function() {
                var i = 2 * J * e + A;
                e = e + d * Math.sqrt(G.speed / (i * i + 1));
                if ((d == 1 && e > K.x) || (d == -1 && e < K.x)) {
                    e = K.x
                }
                var h = e, g = J * h * h + A * h;
                L.setAttribute("data-center", [Math.round(h), Math.round(g)].join());
                if (I == "margin") {
                    L.style.marginLeft = h + "px";
                    L.style.marginTop = g + "px"
                } else {
                    L.style[I] = "translate(" + [h + "px", g + "px"].join() + ")"
                }
                if (e !== K.x) {
                    G.progress(h, g);
                    window.requestAnimationFrame(f)
                } else {
                    G.complete();
                    D = true
                }
            };
            window.requestAnimationFrame(f);
            D = false;
            return this
        };
        b.init = function() {
            this.position().mark().move()
        }
    }
    return b
};
(function() {
    var d = 0;
    var f = ["webkit", "moz"];
    for (var e = 0; e < f.length && !window.requestAnimationFrame; ++e) {
        window.requestAnimationFrame = window[f[e] + "RequestAnimationFrame"];
        window.cancelAnimationFrame = window[f[e] + "CancelAnimationFrame"] || window[f[e] + "CancelRequestAnimationFrame"]
    }
    if (!window.requestAnimationFrame) {
        window.requestAnimationFrame = function(j, c) {
            var i = new Date().getTime();
            var b = Math.max(0, 16.7 - (i - d));
            var a = window.setTimeout(function() {
                j(i + b)
            }, b);
            d = i + b;
            return a
        }
    }
    if (!window.cancelAnimationFrame) {
        window.cancelAnimationFrame = function(a) {
            clearTimeout(a)
        }
    }
}());
(function() {
    var j = window.loli || (window.loli = {});
    var f = window, i = f.document, d, b = "localStorage", k = {};
    k.set = function(e, m) {
    };
    k.get = function(e) {
    };
    k.remove = function(e) {
    };
    k.clear = function() {
    };
    function a() {
        try {
            return (b in f && f[b])
        } catch (e) {
            return false
        }
    }
    if (a()) {
        d = f[b];
        k.set = function(e, m) {
            if (m === undefined) {
                return d.remove(e)
            }
            d.setItem(e, m);
            return m
        };
        k.get = function(e) {
            return d.getItem(e)
        };
        k.remove = function(e) {
            d.removeItem(e)
        };
        k.clear = function() {
            d.clear()
        }
    } else {
        if (i.documentElement.addBehavior) {
            var h, c;
            try {
                c = new ActiveXObject("htmlfile");
                c.open();
                c.write('<script>document.w=window<\/script><iframe src="/favicon.ico"></iframe>');
                c.close();
                h = c.w.frames[0].document;
                d = h.createElement("div")
            } catch (g) {
                d = i.createElement("div");
                h = i.body
            }
            function l(e) {
                return function() {
                    try {
                        var n = Array.prototype.slice.call(arguments, 0);
                        n.unshift(d);
                        h.appendChild(d);
                        d.addBehavior("#default#userData");
                        d.load(b);
                        var m = e.apply(k, n);
                        h.removeChild(d);
                        return m
                    } catch (o) {
                    }
                }
            }
            k.set = l(function(n, e, m) {
                if (m === undefined) {
                    return k.remove(e)
                }
                n.setAttribute(e, m);
                n.save(b);
                return m
            });
            k.get = l(function(m, e) {
                return m.getAttribute(e)
            });
            k.remove = l(function(m, e) {
                m.removeAttribute(e);
                m.save(b)
            });
            k.clear = l(function(p) {
                var n = p.XMLDocument.documentElement.attributes;
                try {
                    p.load(b)
                } catch (e) {
                }
                for (var o = 0, m; m = n[o]; o++) {
                    p.removeAttribute(m.name)
                }
                p.save(b)
            })
        }
    }
    k.isRoot = function() {
        var e = true;
        var o = document.domain;
        var m = /([^\.]*)\.yhd\.com/;
        if (m.test(o)) {
            var n = m.exec(o)[1];
            if (n != "www") {
                e = false
            }
        }
        return e
    };
    k.setFromRoot = function(p, o, s) {
        var q = s || function() {
        };
        if (k.isRoot()) {
            var r = k.set(p, o);
            q({status: 1,key: p,value: r})
        } else {
            if (!window.postMessage || !window.addEventListener) {
                q({status: 0,key: p,value: null});
                return
            }
            var m = "globalLocalStorageAdaptorForSet";
            if ($("#" + m).size() == 0) {
                var t = document.createElement("iframe");
                t.setAttribute("id", m);
                t.setAttribute("style", "display:none");
                t.setAttribute("src", "http://www.yhd.com/html/setLocalStorage.html");
                document.body.appendChild(t);
                $("#" + m).load(function() {
                    var v = $(this).get(0).contentWindow;
                    var u = window.location.protocol + "//www.yhd.com";
                    v.postMessage({key: p,value: o}, u);
                    q({status: 1,key: p,value: o})
                })
            } else {
                var e = $("#" + m).get(0).contentWindow;
                var n = window.location.protocol + "//www.yhd.com";
                e.postMessage({key: p,value: o}, n);
                q({status: 1,key: p,value: o})
            }
        }
    };
    k.getFromRoot = function(r, e) {
        var s = e || function() {
        };
        if (k.isRoot()) {
            var u = k.get(r);
            s({status: 1,key: r,value: u})
        } else {
            if (!window.postMessage || !window.addEventListener) {
                s({status: 0,key: r,value: null});
                return
            }
            var p = "globalLocalStorageAdaptorForGet";
            if ($("#" + p).size() == 0) {
                var m = document.createElement("iframe");
                m.setAttribute("id", p);
                m.setAttribute("style", "display:none");
                m.setAttribute("src", "http://www.yhd.com/html/getLocalStorage.html");
                document.body.appendChild(m);
                $("#" + p).load(function() {
                    var x = $(this).get(0).contentWindow;
                    var v = window.location.protocol + "//www.yhd.com";
                    var w = window.location.protocol + "//" + window.location.host;
                    x.postMessage({key: r,host: w}, v)
                })
            } else {
                var n = $("#" + p).get(0).contentWindow;
                var q = window.location.protocol + "//www.yhd.com";
                var t = window.location.protocol + "//" + window.location.host;
                n.postMessage({key: r,host: t}, q)
            }
            var o = function(w) {
                var v = /^http[s]?:\/\/([^\.]*)\.yhd\.com/i;
                if (v.test(w.origin)) {
                    var x = w.data;
                    if (x) {
                        s({status: 1,key: x.key,value: x.value})
                    }
                    window.removeEventListener("message", o)
                }
            };
            window.addEventListener("message", o)
        }
    };
    j.yhdStore = k
})();
(function(a) {
    a(function() {
        var j = window.loli || (window.loli = {});
        j.app = j.app || {};
        var s = j.app.prism = j.app.prism || {};
        var e = s.functions = s.functions || {};
        var b = e.registerEvent = e.registerEvent || {};
        var n = e.openEvent = e.openEvent || {};
        var l = e.closeEvent = e.closeEvent || {};
        var q = 1;
        var k = 2;
        var m = 3;
        var p = "http://image.yihaodianimg.com/statics/global/images/defaultproduct_40x40.jpg";
        var d = 0;
        e.registerEvent.order = function(t) {
            var D = a(t.prismLayoutId);
            var C = a(t.prismOrderIconId);
            var w = a(t.prismOrderId);
            var A = t.prismOrderFlag;
            var B = t.pageCode;
            var z = t.prismTrackerPrefix;
            if (!A) {
                C.click(function() {
                    if (w.data("dataLoaded") == "1" && w.css("display") != "none") {
                        g(w, D);
                        return
                    }
                    w.data("dataLoaded", "1");
                    var F = {"0": 0,"1": 0,"2": 0,"3": 0};
                    var E = f(F, t);
                    w.append(E);
                    h(w, D);
                    a("a.item_btn_close", w).click(function() {
                        g(w, D)
                    });
                    var E = [];
                    E.push("<div class='item_empty'>");
                    E.push("<p><i class='icon_bg empty_orders'></i></p>");
                    E.push("<p>此功能尚未开放!</p>");
                    E.push("</div>");
                    a("#prismUnpayOrderList>div.item_box").removeClass("global_loading").html(E.join(""));
                    if (C.data("clicked") != 1) {
                        gotracker("2", z + "_prism_order");
                        C.data("clicked", 1)
                    }
                });
                return
            }
            C.click(function() {
                if (w.data("dataLoaded") == "1" && w.css("display") != "none") {
                    g(w, D);
                    return
                }
                var E = function(G) {
                    if (G.result == 1) {
                        if (w.data("dataLoaded") == "1") {
                            h(w, D)
                        } else {
                            w.data("dataLoaded", "1");
                            var I = w.data("ordersNumData");
                            var H = f(I, t);
                            w.append(H);
                            h(w, D);
                            a("div.tabs_box ul li", w).click(function() {
                                y(a(this), a("div.tabs_list", w))
                            });
                            a("a.item_btn_close", w).click(function() {
                                g(w, D)
                            });
                            var F = v(I);
                            y(a("div.tabs_box ul li", w).eq(F), a("div.tabs_list", w))
                        }
                    } else {
                        if (yhdPublicLogin) {
                            yhdPublicLogin.showLoginDiv()
                        }
                    }
                };
                j.globalCheckLogin(E);
                if (C.data("clicked") != 1) {
                    gotracker("2", z + "_prism_order");
                    C.data("clicked", 1)
                }
            });
            var v = function(G) {
                var F = G != null ? (G["1"] != null ? G["1"] : 0) : 0;
                var E = G != null ? (G["2"] != null ? G["2"] : 0) : 0;
                var H = G != null ? (G["3"] != null ? G["3"] : 0) : 0;
                if (F != 0) {
                    return 0
                }
                if (E != 0) {
                    return 1
                }
                if (H != 0) {
                    return 2
                }
                return 0
            };
            var y = function(I, H) {
                var E = I.index();
                I.siblings().removeClass("cur");
                I.addClass("cur");
                H.hide();
                H.eq(E).show();
                var F = I.data("tabLoaded");
                if (E == 0) {
                    if (!F) {
                        I.data("tabLoaded", 1);
                        u(q)
                    }
                } else {
                    if (E == 1) {
                        if (!F) {
                            I.data("tabLoaded", 1);
                            u(k)
                        }
                    } else {
                        if (E == 2) {
                            if (!F) {
                                I.data("tabLoaded", 1);
                                u(m)
                            }
                        }
                    }
                }
                if (I.data("clicked") != 1) {
                    var G = z;
                    if (E == 0) {
                        G += "_prism_order_notpaid_tab"
                    } else {
                        if (E == 1) {
                            G += "_prism_order_paid_tab"
                        } else {
                            if (E == 1) {
                                G += "_prism_order_com_tab"
                            }
                        }
                    }
                    gotracker("2", G);
                    I.data("clicked", 1)
                }
            };
            var u = function(G) {
                var F = URLPrefix.central + "/homepage/ajaxFindNewPrismOrders.do?callback=?";
                var E = function(I) {
                    w.data("ordersData-" + G, I);
                    var J = x(I, G);
                    if (G == q) {
                        a("#prismUnpayOrderList>div.item_box").removeClass("global_loading").html(J);
                        i(a("#prismUnpayOrderList>div.item_box"));
                        c(a("#prismUnpayOrderList"), D);
                        a("#prismUnpayOrderList div.item_list").each(function() {
                            o(a(this))
                        })
                    } else {
                        if (G == k) {
                            a("#prismUnreceiptOrderList>div.item_box").removeClass("global_loading").html(J);
                            i(a("#prismUnreceiptOrderList>div.item_box"));
                            c(a("#prismUnreceiptOrderList"), D);
                            a("#prismUnreceiptOrderList div.item_list").each(function() {
                                o(a(this))
                            })
                        } else {
                            if (G == m) {
                                a("#prismUncommentOrderList>div.item_box").removeClass("global_loading").html(J);
                                i(a("#prismUncommentOrderList>div.item_box"));
                                c(a("#prismUncommentOrderList"), D);
                                a("#prismUncommentOrderList div.item_list").each(function() {
                                    o(a(this))
                                })
                            }
                        }
                    }
                };
                var H = {userId: t.userId,status: G,currSiteId: t.currSiteId,currSiteType: t.currSiteType,provinceId: t.provinceId,pageCode: t.pageCode};
                a.getJSON(F, H, function(I) {
                    var K = I;
                    if (K) {
                        if (K.status == 1) {
                            var J = K.orders;
                            E(J)
                        }
                    }
                })
            };
            var x = function(R, E) {
                var O = typeof URLPrefix.my != "undefined" ? URLPrefix.my : "http://my.yhd.com";
                var F = O + "/order/myOrder.do";
                var H = [];
                if (!R || R.length == 0) {
                    H.push("<div class='item_empty'>");
                    H.push("<p><i class='icon_bg empty_orders'></i></p>");
                    H.push("<p>您还没有订单哦~</p>");
                    H.push("</div>");
                    return H.join("")
                }
                for (var M = 0; M < R.length; M++) {
                    var L = R[M];
                    var T = E == q ? "待付款" : (E == k ? "待收货" : (E == m ? "待评论" : ""));
                    var U = O + "/order/orderDetail.do?orderCode=" + L.code;
                    var P = "";
                    if (E == q) {
                        P = z + "_prism_order_notpaid_o_" + L.id
                    } else {
                        if (E == k) {
                            P = z + "_prism_order_paid_o_" + L.id
                        } else {
                            if (E == m) {
                                P = z + "_prism_order_com_o_" + L.id
                            }
                        }
                    }
                    H.push("<div class='item_list'>");
                    if (!L.hasSubOrders) {
                        if (E == k && L.deliveryMsg) {
                            T = L.deliveryMsg
                        }
                        H.push("<h1><span>" + T + "</span></h1>");
                        H.push("<div class='pro_box'>");
                        H.push("<div class='pro_pic'>");
                        H.push("<ul class='clearfix'>");
                        for (var K = 0; K < L.items.length; K++) {
                            var S = "http://item-home.yhd.com/item/snapshotShow.do?productId=" + L.items[K].productId + "&soItemId=" + L.items[K].soItemId + "&flag=1";
                            var Q = "";
                            var G = L.items[K].productPicPath ? L.items[K].productPicPath : p;
                            if (E == q) {
                                Q = z + "_prism_order_notpaid_p_" + L.items[K].productMerchantId
                            } else {
                                if (E == k) {
                                    Q = z + "_prism_order_paid_p_" + L.items[K].productMerchantId
                                } else {
                                    if (E == m) {
                                        Q = z + "_prism_order_com_p_" + L.items[K].productMerchantId
                                    }
                                }
                            }
                            H.push("<li><a href='" + S + "' data-ref='" + Q + "' target='_blank' title='" + L.items[K].productName + "'><img src='" + G + "' alt='" + L.items[K].productName + "'/></a></li>")
                        }
                        H.push("</ul>");
                        H.push("</div>");
                        H.push("<a href='javascript:;' class='icon_bg btn_prev' style='display:none;'></a>");
                        H.push("<a href='javascript:;' class='icon_bg btn_next' style='display:none;'></a>");
                        H.push("<a href='" + U + "' data-ref='" + P + "' target='_blank' class='pro_orderNo'>订单号" + L.code + "</a>");
                        if (E == m) {
                            var N = "http://e.yhd.com/front-pe/pe/orderProductExperience!orderProductExperience.do?soId=" + L.id + "&userId=" + t.userId + "&hasCommented=false&soType=0";
                            var I = z + "_prism_order_com_to_" + L.id;
                            H.push("<a class='pro_comment' target='_blank' href='" + N + "' data-ref='" + I + "'>立即评论</a>")
                        }
                        H.push("</div>")
                    } else {
                        var V = L.subOrders[0];
                        for (var J = 0; J < L.subOrders.length; J++) {
                            if (E == q && L.subOrders[J].status == "3") {
                                V = L.subOrders[J];
                                break
                            } else {
                                if (E == k && L.subOrders[J].status == "20") {
                                    V = L.subOrders[J];
                                    break
                                } else {
                                    if (E == m) {
                                        for (var K = 0; K < L.subOrders[J].items.length; K++) {
                                            if (L.subOrders[J].items[K].remarkId == null || L.subOrders[J].items[K].remarkId == "" || L.subOrders[J].items[K].remarkId == 0) {
                                                V = L.subOrders[J];
                                                break
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (E == k && V.deliveryMsg) {
                            T = V.deliveryMsg
                        }
                        H.push("<h1><span>" + T + "</span></h1>");
                        H.push("<div class='pro_box'>");
                        H.push("<div class='pro_pic'>");
                        H.push("<ul class='clearfix'>");
                        for (var K = 0; K < V.items.length; K++) {
                            var S = "http://item-home.yhd.com/item/snapshotShow.do?productId=" + V.items[K].productId + "&soItemId=" + V.items[K].soItemId + "&flag=1";
                            var Q = "";
                            var G = V.items[K].productPicPath ? V.items[K].productPicPath : p;
                            if (E == q) {
                                Q = z + "_prism_order_notpaid_p_" + V.items[K].productMerchantId
                            } else {
                                if (E == k) {
                                    Q = z + "_prism_order_paid_p_" + V.items[K].productMerchantId
                                } else {
                                    if (E == m) {
                                        Q = z + "_prism_order_com_p_" + V.items[K].productMerchantId
                                    }
                                }
                            }
                            H.push("<li><a href='" + S + "' data-ref='" + P + "' target='_blank' title='" + V.items[K].productName + "'><img src='" + G + "' alt='" + V.items[K].productName + "'/></a></li>")
                        }
                        H.push("</ul>");
                        H.push("</div>");
                        H.push("<a href='javascript:;' class='icon_bg btn_prev' style='display:none;'></a>");
                        H.push("<a href='javascript:;' class='icon_bg btn_next' style='display:none;'></a>");
                        H.push("<a href='" + U + "' data-ref='" + P + "' target='_blank' class='pro_orderNo'>订单号" + L.code + "</a>");
                        if (E == m) {
                            var N = "http://e.yhd.com/front-pe/pe/orderProductExperience!orderProductExperience.do?soId=" + L.id + "&userId=" + t.userId + "&hasCommented=false&soType=0";
                            var I = z + "_prism_order_com_to_" + L.id;
                            H.push("<a class='pro_comment' target='_blank' href='" + N + "' data-ref='" + I + "'>立即评论</a>")
                        }
                        H.push("</div>")
                    }
                    H.push("</div>")
                }
                return H.join("")
            }
        };
        e.openEvent.order = function(w) {
            var u = a(w.prismLayoutId);
            var v = a(w.prismOrderIconId);
            var t = a(w.prismOrderId);
            var x = w.prismOrderFlag;
            if (!x) {
                return
            }
            if (!t.data("numsLoaded")) {
                r(w, v, t);
                t.data("numsLoaded", "1");
                recordTrackInfoWithType("1", w.prismTrackerPrefix + "_prism_order_show")
            }
        };
        e.closeEvent.order = function(w) {
            var u = a(w.prismLayoutId);
            var v = a(w.prismOrderIconId);
            var t = a(w.prismOrderId);
            var x = w.prismOrderFlag;
            if (!x) {
                return
            }
            g(t, u)
        };
        var f = function(C, A) {
            var w = A.pageCode;
            var u = A.prismTrackerPrefix;
            var F = C != null ? (C["1"] != null ? C["1"] : 0) : 0;
            var v = C != null ? (C["2"] != null ? C["2"] : 0) : 0;
            var B = C != null ? (C["3"] != null ? C["3"] : 0) : 0;
            var y = typeof URLPrefix.my != "undefined" ? URLPrefix.my : "http://my.yhd.com";
            var z = y + "/order/myOrder.do";
            var t = u + "_prism_order_notpaid_more";
            var E = u + "_prism_order_paid_more";
            var x = u + "_prism_order_com_more";
            var D = [];
            D.push("<div class='info_item info_box'>");
            D.push("<div class='tabs_box'>");
            D.push("<ul class='clearfix'>");
            D.push("<li class='cur' " + (d ? "style='width:135px;'" : "") + ">待付款 " + (F > 999 ? "999+" : (F != 0 ? F : "")) + "</li>");
            D.push("<li " + (d ? "style='width:135px;'" : "") + ">待收货 " + (v > 999 ? "999+" : (v != 0 ? v : "")) + "</li>");
            if (!d) {
                D.push("<li>待评论 " + (B > 999 ? "999+" : (B != 0 ? B : "")) + "</li>")
            }
            D.push("</ul>");
            D.push("</div>");
            D.push("<div class='tabs_list' id='prismUnpayOrderList'>");
            D.push("<div class='item_box scroll-pane global_loading'>");
            D.push("</div>");
            D.push("<a href='" + z + "' data-ref='" + t + "' target='_blank' class='btn_all'>查看所有</a>");
            D.push("</div>");
            D.push("<div class='tabs_list' id='prismUnreceiptOrderList' style='display:none;'>");
            D.push("<div class='item_box scroll-pane global_loading'>");
            D.push("</div>");
            D.push("<a href='" + z + "' data-ref='" + E + "' target='_blank' class='btn_all'>查看所有</a>");
            D.push("</div>");
            D.push("<div class='tabs_list' id='prismUncommentOrderList' style='display:none;'>");
            D.push("<div class='item_box scroll-pane global_loading'>");
            D.push("</div>");
            D.push("<a href='" + y + "/member/exp/comment.do' data-ref='" + x + "' target='_blank' class='btn_all'>查看所有</a>");
            D.push("</div>");
            D.push("<a class='icon_bg item_btn_close' href='javascript:;'>close</a>");
            D.push("</div>");
            return D.join("")
        };
        var r = function(t, y, w) {
            var x = URLPrefix.central + "/homepage/ajaxFindNewPrismOrdersNum.do?callback=?";
            var u = function(z) {
                if (d && z["3"] != null) {
                    z["0"] = z["0"] - z["3"]
                }
                w.data("ordersNumData", z);
                if (z["0"] > 0) {
                    y.html("<span class='icon_bg bubble_tips'>" + z["0"] + "</span>");
                    a("span", y).show(500)
                }
            };
            var v = {userId: t.userId,currSiteId: t.currSiteId,currSiteType: t.currSiteType,provinceId: t.provinceId,pageCode: t.pageCode};
            a.getJSON(x, v, function(z) {
                var B = z;
                if (B) {
                    if (B.status == 1) {
                        var A = B.result;
                        u(A)
                    }
                }
            })
        };
        var h = function(u, t) {
            u.data("clicked", 1);
            a("div.nav_box", t).hide();
            u.show().find(".info_item").hide().show(500)
        };
        var g = function(u, t) {
            u.hide()
        };
        var c = function(u, t) {
            a("div.scroll-pane", u).jScrollPane()
        };
        var o = function(t) {
            var w = a("a.btn_prev", t);
            var z = a("a.btn_next", t);
            var u = t.find(".pro_pic ul");
            var y = a("div.pro_pic li", t).size();
            var A = 4;
            var v = (y % A == 0) ? Math.floor(y / A) : Math.floor(y / A) + 1;
            var x = 1;
            if (v > 1) {
                w.show();
                z.addClass("btn_next_true").show();
                w.click(function() {
                    if (x > 1) {
                        u.animate({left: "-" + (x - 2) * 200 + "px"}, function() {
                            x--;
                            if (x < v) {
                                z.addClass("btn_next_true")
                            }
                            if (x == 1) {
                                w.removeClass("btn_prev_true")
                            }
                        })
                    } else {
                        w.removeClass("btn_prev_true");
                        z.addClass("btn_next_true")
                    }
                });
                z.click(function() {
                    if (x < v) {
                        u.animate({left: "-" + (x) * 200 + "px"}, function() {
                            x++;
                            if (x > 1) {
                                w.addClass("btn_prev_true")
                            }
                            if (x == v) {
                                z.removeClass("btn_next_true")
                            }
                        })
                    } else {
                        w.addClass("btn_prev_true");
                        z.removeClass("btn_next_true")
                    }
                })
            }
        };
        var i = function(v) {
            var t = a(window).height();
            var u = v.siblings(".btn_all").length > 1 ? 100 : 100 + 45;
            var x = parseInt(v.parents(".nav_box").css("bottom")) + u;
            var w = t - x;
            if (t < x + v.height()) {
                v.css("height", w)
            }
        }
    })
})(jQuery);
(function(a) {
    a(function() {
        var f = window.loli || (window.loli = {});
        f.app = f.app || {};
        var b = f.app.prism = f.app.prism || {};
        var k = b.functions = b.functions || {};
        var c = k.registerEvent = k.registerEvent || {};
        var j = k.openEvent = k.openEvent || {};
        var e = k.closeEvent = k.closeEvent || {};
        k.registerEvent.coupon = function(q) {
            var p = a(q.prismLayoutId);
            var o = a(q.prismCouponIconId);
            var t = a(q.prismCouponId);
            var v = q.prismCouponFlag;
            var n = q.pageCode;
            var s = q.prismTrackerPrefix;
            if (!v) {
                o.click(function() {
                    if (t.data("dataLoaded") == "1" && t.css("display") != "none") {
                        h(t, p);
                        return
                    }
                    t.data("dataLoaded", "1");
                    var w = 0;
                    var x = m(w, q);
                    t.append(x);
                    l(t, p);
                    a("a.item_btn_close", t).click(function() {
                        h(t, p)
                    });
                    var x = [];
                    x.push("<div class='item_empty'>");
                    x.push("<p><i class='icon_bg empty_gift'></i></p>");
                    x.push("<p>此功能尚未开放!</p>");
                    x.push("</div>");
                    a("#prismCouponList>div.item_box").removeClass("global_loading").html(x.join(""));
                    if (o.data("clicked") != 1) {
                        gotracker("2", s + "_prism_coupon");
                        o.data("clicked", 1)
                    }
                });
                return
            }
            o.click(function() {
                if (t.data("dataLoaded") == "1" && t.css("display") != "none") {
                    h(t, p);
                    return
                }
                var w = function(y) {
                    if (y.result == 1) {
                        if (t.data("dataLoaded") == "1") {
                            l(t, p)
                        } else {
                            t.data("dataLoaded", "1");
                            var z = t.data("couponsNumData");
                            var x = m(z, q);
                            t.append(x);
                            l(t, p);
                            a("a.item_btn_close", t).click(function() {
                                h(t, p)
                            });
                            r()
                        }
                    } else {
                        if (yhdPublicLogin) {
                            yhdPublicLogin.showLoginDiv()
                        }
                    }
                };
                f.globalCheckLogin(w);
                if (o.data("clicked") != 1) {
                    gotracker("2", s + "_prism_coupon");
                    o.data("clicked", 1)
                }
            });
            var r = function() {
                var x = URLPrefix.central + "/homepage/ajaxFindNewPrismCoupons.do?callback=?";
                var z = t.data("couponsNumData");
                var y = function(B) {
                    t.data("couponsData", B);
                    var A = u(B);
                    a("#prismCouponList>div.item_box").removeClass("global_loading").html(A);
                    g(a("#prismCouponList>div.item_box"));
                    d(a("#prismCouponList"), p)
                };
                var w = {userId: q.userId,total: z != null ? z : 50,currSiteId: q.currSiteId,currSiteType: q.currSiteType,provinceId: q.provinceId,pageCode: q.pageCode};
                a.getJSON(x, w, function(A) {
                    var C = A;
                    if (C) {
                        if (C.status == 1) {
                            var B = C.coupons;
                            y(B)
                        }
                    }
                })
            };
            var u = function(A) {
                var y = typeof URLPrefix.my != "undefined" ? URLPrefix.my : "http://my.yhd.com";
                var x = y + "/coupon/displayCoupons.do";
                var D = [];
                if (A && A.length > 0) {
                    for (var B = 0; B < A.length; B++) {
                        var z = A[B];
                        var F = z.timeType == 1 ? (z.endDateTimeStr + " 结束") : (z.startDateTimeStr + " 开始");
                        var C = "http://search.yhd.com/redirectCoupon/" + z.couponActiveDefId;
                        var E = s + "_prism_coupon_" + z.couponNumber;
                        var w = z.timeType == 1 ? "icon_bg vouchers_list vou_2" : (z.timeType == 2 ? "icon_bg vouchers_list vou_3" : "icon_bg vouchers_list vou_1");
                        if (z.couponUserType == 0 || z.couponUserType == 5 || z.couponUserType == 6) {
                            C = x
                        }
                        if (z.timeType == 1) {
                            if (z.dateDiff == 0) {
                                F = "今天到期"
                            } else {
                                F = "还剩 " + z.dateDiff + "天 到期"
                            }
                        } else {
                            if (z.timeType == 2) {
                                F = z.startDateStr + " 至 " + z.endDateStr
                            } else {
                                if (z.timeType == 3) {
                                    F = z.startDateTimeStr + " 开始"
                                }
                            }
                        }
                        D.push("<div class='" + w + "'>");
                        D.push("<p class='price'><em>&yen;</em>" + z.amount + "</p>");
                        D.push("<p class='text'><a href='" + C + "' data-ref='" + E + "' target='_blank' title='" + z.couponInfo + "'>" + z.couponInfo + "</a></p>");
                        D.push("<p class='time'>" + F + "</p>");
                        D.push("</div>")
                    }
                } else {
                    D.push("<div class='item_empty'>");
                    D.push("<p><i class='icon_bg empty_gift'></i></p>");
                    D.push("<p>您还没有礼券哦~</p>");
                    D.push("</div>")
                }
                return D.join("")
            }
        };
        k.openEvent.coupon = function(o) {
            var n = a(o.prismLayoutId);
            var r = a(o.prismCouponIconId);
            var q = a(o.prismCouponId);
            var p = o.prismCouponFlag;
            if (!p) {
                return
            }
            if (!q.data("numsLoaded")) {
                i(o, r, q);
                q.data("numsLoaded", "1");
                recordTrackInfoWithType("1", o.prismTrackerPrefix + "_prism_coupon_show")
            }
        };
        k.closeEvent.coupon = function(o) {
            var n = a(o.prismLayoutId);
            var r = a(o.prismCouponIconId);
            var q = a(o.prismCouponId);
            var p = o.prismCouponFlag;
            if (!p) {
                return
            }
            h(q, n)
        };
        var m = function(o, n) {
            var q = typeof URLPrefix.my != "undefined" ? URLPrefix.my : "http://my.yhd.com";
            var r = q + "/coupon/displayCoupons.do";
            var t = n.prismTrackerPrefix + "_prism_coupon_more";
            var s = o ? o : 0;
            var p = [];
            p.push("<div class='info_item info_box'>");
            p.push("<div class='tabs_box'>");
            p.push("<ul class='clearfix'>");
            p.push("<li class='cur'>我的抵用券 " + (s > 999 ? "999+" : (s != 0 ? s : "")) + "</li>");
            p.push("</ul>");
            p.push("</div>");
            p.push("<div class='tabs_list' id='prismCouponList'>");
            p.push("<div class='item_box scroll-pane global_loading'>");
            p.push("</div>");
            p.push("<a href='" + r + "' data-ref='" + t + "' target='_blank' class='btn_all'>查看所有</a>");
            p.push("</div>");
            p.push("<a class='icon_bg item_btn_close' href='javascript:;'>close</a>");
            p.push("</div>");
            return p.join("")
        };
        var i = function(p, s, r) {
            var o = URLPrefix.central + "/homepage/ajaxFindNewPrismCouponsNum.do?callback=?";
            var n = function(t) {
                r.data("couponsNumData", t);
                if (t > 0) {
                    s.html("<span class='icon_bg bubble_tips'>" + t + "</span>");
                    a("span", s).show(500)
                }
            };
            var q = {userId: p.userId,currSiteId: p.currSiteId,currSiteType: p.currSiteType,provinceId: p.provinceId,pageCode: p.pageCode};
            a.getJSON(o, q, function(t) {
                var v = t;
                if (v) {
                    if (v.status == 1) {
                        var u = v.nums;
                        n(u)
                    }
                }
            })
        };
        var l = function(o, n) {
            o.data("clicked", 1);
            a("div.nav_box", n).hide();
            o.show().find(".info_item").hide().show(500)
        };
        var h = function(o, n) {
            o.hide()
        };
        var d = function(o, n) {
            a("div.scroll-pane", o).jScrollPane()
        };
        var g = function(p) {
            var n = a(window).height();
            var o = p.siblings(".btn_all").length > 1 ? 100 : 100 + 45;
            var r = parseInt(p.parents(".nav_box").css("bottom")) + o;
            var q = n - r;
            if (n < r + p.height()) {
                p.css("height", q)
            }
        }
    })
})(jQuery);
(function(a) {
    a(function() {
        var h = window.loli || (window.loli = {});
        h.app = h.app || {};
        var f = h.app.prism = h.app.prism || {};
        var d = f.functions = f.functions || {};
        var j = d.registerEvent = d.registerEvent || {};
        var c = d.openEvent = d.openEvent || {};
        var i = d.closeEvent = d.closeEvent || {};
        var q = d.resetEvent = d.resetEvent || {};
        var o = 0;
        var m = 1;
        var g = 2;
        d.registerEvent.favorite = function(r) {
            var t = r.provinceId;
            var y = a(r.prismLayoutId);
            var B = a(r.prismFavoriteIconId);
            var x = a(r.prismFavoriteId);
            var z = r.prismFavoriteFlag;
            var w = r.pageCode;
            var u = r.prismTrackerPrefix;
            B.click(function() {
                if (x.data("dataLoaded") == "1" && x.css("display") != "none") {
                    p(x, y);
                    return
                }
                var C = function(E) {
                    if (E.result == 1) {
                        if (x.data("dataLoaded") == "1") {
                            e(x, y)
                        } else {
                            x.data("dataLoaded", "1");
                            var G = x.data("favNumsData");
                            var F = A(G);
                            x.html(F);
                            e(x, y);
                            a("div.tabs_box ul li", x).click(function() {
                                v(a(this), a("div.tabs_list", x))
                            });
                            a("a.item_btn_close", x).click(function() {
                                p(x, y)
                            });
                            var D = s(G);
                            v(a("div.tabs_box ul li", x).eq(D), a("div.tabs_list", x))
                        }
                    } else {
                        if (yhdPublicLogin) {
                            yhdPublicLogin.showLoginDiv()
                        }
                    }
                };
                h.globalCheckLogin(C);
                if (B.data("clicked") != 1) {
                    gotracker("2", u + "_prism_fav");
                    B.data("clicked", 1)
                }
            });
            var s = function(F) {
                var C = F != null ? (F["0"] != null ? F["0"] : 0) : 0;
                var E = F != null ? (F["1"] != null ? F["1"] : 0) : 0;
                var D = F != null ? (F["2"] != null ? F["2"] : 0) : 0;
                if (C != 0) {
                    return 0
                }
                if (E != 0) {
                    return 1
                }
                if (D != 0) {
                    return 2
                }
                return 0
            };
            var v = function(G, F) {
                var C = G.index();
                G.siblings().removeClass("cur");
                G.addClass("cur");
                F.hide();
                F.eq(C).show();
                var D = G.data("isTabLoaded");
                if (C == 0) {
                    if (!D) {
                        G.data("isTabLoaded", 1);
                        b(r, o)
                    }
                } else {
                    if (C == 1) {
                        if (!D) {
                            G.data("isTabLoaded", 1);
                            b(r, m)
                        }
                    } else {
                        if (C == 2) {
                            if (!D) {
                                G.data("isTabLoaded", 1);
                                b(r, g)
                            }
                        }
                    }
                }
                if (G.data("clicked") != 1) {
                    var E = u;
                    if (C == 0) {
                        E += "_prism_fav_pro_tab"
                    } else {
                        if (C == 1) {
                            E += "_prism_fav_shop_tab"
                        } else {
                            if (C == 2) {
                                E += "_prism_fav_brand_tab"
                            }
                        }
                    }
                    gotracker("2", E);
                    G.data("clicked", 1)
                }
            };
            var A = function(E) {
                var I = E != null ? (E["0"] != null ? E["0"] : 0) : 0;
                var H = E != null ? (E["1"] != null ? E["1"] : 0) : 0;
                var C = E != null ? (E["2"] != null ? E["2"] : 0) : 0;
                var J = typeof URLPrefix.my != "undefined" ? URLPrefix.my : "http://my.yhd.com";
                var D = J + "/member/myNewCollection/myFavorite.do";
                var K = u + "_prism_fav_pro_more";
                var L = u + "_prism_fav_shop_more";
                var G = u + "_prism_fav_brand_more";
                var F = [];
                F.push("<div class='info_item info_box'>");
                F.push("<div class='tabs_box'>");
                F.push("<ul class='clearfix'>");
                F.push("<li class='cur'>商品收藏  " + (I > 99 ? "99+" : (I != 0 ? I : "")) + "</li>");
                F.push("<li>店铺收藏  " + (H > 99 ? "99+" : (H != 0 ? H : "")) + "</li>");
                F.push("<li>关注品牌  " + (C > 99 ? "99+" : (C != 0 ? C : "")) + "</li>");
                F.push("</ul>");
                F.push("</div>");
                F.push("<div class='tabs_list' id='proFavList'>");
                F.push("<div class='item_box scroll-pane global_loading'>");
                F.push("</div>");
                F.push("<a href='" + D + "' data-ref='" + K + "' target='_blank' class='btn_all'>查看所有</a>");
                F.push("</div>");
                F.push("<div class='tabs_list' id='shopFavList' style='display:none;'>");
                F.push("<div class='item_box scroll-pane global_loading'>");
                F.push("</div>");
                F.push("<a href='" + D + "' data-ref='" + L + "'  target='_blank' class='btn_all'>查看所有</a>");
                F.push("</div>");
                F.push("<div class='tabs_list' id='brandFavList' style='display:none;'>");
                F.push("<div class='item_box scroll-pane global_loading'>");
                F.push("</div>");
                F.push("<a href='" + D + "' data-ref='" + G + "'  target='_blank' class='btn_all'>查看所有</a>");
                F.push("</div>");
                F.push("<a class='icon_bg item_btn_close' href='javascript:;'>close</a>");
                F.push("</div>");
                return F.join("")
            }
        };
        d.openEvent.favorite = function(s) {
            var r = a(s.prismFavoriteIconId);
            var t = a(s.prismFavoriteId);
            var u = s.prismFavoriteFlag;
            if (!u) {
                return
            }
            if (!t.data("numsLoaded")) {
                k(s, r, t);
                t.data("numsLoaded", "1");
                recordTrackInfoWithType("1", s.prismTrackerPrefix + "_prism_fav_show")
            }
        };
        d.closeEvent.favorite = function(s) {
            var r = a(s.prismLayoutId);
            var t = a(s.prismFavoriteId);
            var u = s.prismFavoriteFlag;
            if (!u) {
                return
            }
            p(t, r)
        };
        d.resetEvent.favorite = function(t) {
            var s = a(t.prismLayoutId);
            var u = a(t.prismFavoriteId);
            var r = a(t.prismFavoriteIconId);
            var v = t.prismFavoriteFlag;
            if (!v) {
                return
            }
            if (u.data("numsLoaded")) {
                k(t, r, u)
            }
            if (u.data("dataLoaded") == "1") {
                u.data("dataLoaded", 0)
            }
        };
        var k = function(t, r, w) {
            var u = typeof URLPrefix.my != "undefined" ? URLPrefix.my : "http://my.yhd.com";
            var s = u + "/member/myNewFavorite/myUserFavoriteNum.do?callback=?";
            var v = function(C) {
                w.data("favNumsData", C);
                var y = 0;
                if (C) {
                    try {
                        var A = C != null ? (C["0"] != null ? C["0"] : 0) : 0;
                        var x = C != null ? (C["1"] != null ? C["1"] : 0) : 0;
                        var B = C != null ? (C["2"] != null ? C["2"] : 0) : 0;
                        y = parseInt(A) + parseInt(x) + parseInt(B)
                    } catch (z) {
                    }
                }
                if (y) {
                    r.html("<span class='icon_bg bubble_tips'>" + (y > 999 ? "999+" : y) + "</span>")
                }
                a("span", r).show(500)
            };
            a.getJSON(s, null, function(x) {
                var z = x;
                if (z) {
                    if (z.code == 0) {
                        var y = z.resultMap;
                        v(y)
                    }
                }
            })
        };
        var b = function(z, B) {
            var D = z.provinceId;
            var y = a(z.prismLayoutId);
            var v = a(z.prismFavoriteIconId);
            var E = a(z.prismFavoriteId);
            var r = z.prismFavoriteFlag;
            var G = z.pageCode;
            var F = z.prismTrackerPrefix;
            if (!r) {
                var C = [];
                C.push("<div class='item_empty'>");
                C.push("<p><i class='icon_bg empty_favorite'></i></p>");
                C.push("<p>抱歉，系统繁忙，请稍候再试</p>");
                C.push("</div>");
                if (B == o) {
                    a("#proFavList>div.item_box").removeClass("global_loading").html(C.join(""))
                } else {
                    if (B == m) {
                        a("#shopFavList>div.item_box").removeClass("global_loading").html(C.join(""))
                    } else {
                        if (B == g) {
                            a("#brandFavList>div.item_box").removeClass("global_loading").html(C.join(""))
                        }
                    }
                }
                return
            }
            var t = typeof URLPrefix.my != "undefined" ? URLPrefix.my : "http://my.yhd.com";
            var u = t + "/member/myNewFavorite/myUserFavoriteInfo.do?callback=?";
            var s = function(I) {
                E.data("favsData-" + B, I);
                var J = w(I, B);
                if (B == o) {
                    a("#proFavList>div.item_box").removeClass("global_loading").html(J);
                    x(I);
                    n(a("#proFavList>div.item_box"));
                    l(a("#proFavList"), y)
                } else {
                    if (B == m) {
                        a("#shopFavList>div.item_box").removeClass("global_loading").html(J);
                        n(a("#shopFavList>div.item_box"));
                        l(a("#shopFavList"), y)
                    } else {
                        if (B == g) {
                            a("#brandFavList>div.item_box").removeClass("global_loading").html(J);
                            n(a("#brandFavList>div.item_box"));
                            l(a("#brandFavList"), y)
                        }
                    }
                }
            };
            var A = {currentPage: 1,pageSize: 20,favoriteType: B};
            a.getJSON(u, A, function(I) {
                var K = I;
                if (K) {
                    if (K.code == 0) {
                        var J = K.resultList;
                        s(J)
                    } else {
                        var J = [];
                        s(J)
                    }
                }
            });
            var w = function(P, J) {
                var Q = [];
                if (!P || P.length < 1) {
                    Q.push("<div class='item_empty'>");
                    Q.push("<p><i class='icon_bg empty_favorite'></i></p>");
                    Q.push("<p>您还没有收藏哦~</p>");
                    Q.push("</div>")
                } else {
                    for (var L = 0; L < P.length; L++) {
                        var S = P[L];
                        var U = "";
                        if (J == o) {
                            if (!S.price || !S.listPrice) {
                                continue
                            }
                            U = F + "_prism_fav_pro_p_" + S.pmInfoId;
                            var V = "http://item.yhd.com/item/" + S.pmInfoId;
                            var N = yhdToolKit.getProductPicByDefaultPic(S.productUrl, 60, 60);
                            Q.push("<div class='item_list' id='prism_fav_pm_" + S.pmInfoId + "'>");
                            Q.push("<dl class='clearfix'>");
                            Q.push("<dt><a href='" + V + "' data-ref='" + U + "' target='_blank'><img src='" + N + "' /></a></dt>");
                            Q.push("<dd><a href='" + V + "' data-ref='" + U + "' target='_blank'>" + S.productName + "</a></dd>");
                            Q.push("<dd>");
                            Q.push("<span class='price_1'>&yen;" + S.price + "</span>");
                            Q.push("<span class='price_2'>&yen;" + S.listPrice + "</span>");
                            Q.push("</dd>");
                            Q.push("</dl>");
                            Q.push("</div>")
                        } else {
                            if (J == m) {
                                U = F + "_prism_shop_logo_" + S.merchantId;
                                var V = "http://shop.yhd.com/merchantfront/accessAction.action?siteId=1&merchantId=" + S.merchantId;
                                var R = "target='_blank'";
                                var M = 0;
                                var O = yhdToolKit.getProductPicByDefaultPic(S.logoUrl, 60, 60);
                                if (S.merchantId < 300) {
                                    V = "javascript:;";
                                    R = "";
                                    M = 1
                                }
                                Q.push("<div class='item_list'>");
                                Q.push("<dl class='clearfix'>");
                                Q.push("<dt><a href='" + V + "' data-ref='" + U + "' " + R + "><img src='" + O + "' /></a></dt>");
                                Q.push("<dd class='text'><a href='" + V + "' data-ref='" + U + "' " + R + ">" + S.merchantName + "</a></dd>");
                                if (!M) {
                                    Q.push("<dd>");
                                    Q.push("<a href='" + V + "' data-ref='" + U + "' " + R + ">进店逛逛</a>");
                                    Q.push("</dd>")
                                }
                                Q.push("</dl>");
                                Q.push("</div>")
                            } else {
                                if (J == g) {
                                    U = F + "_prism_brand_logo_" + S.manageBrandId;
                                    var T = F + "_prism_brand_a_" + S.manageBrandId;
                                    var K = F + "_prism_brand_b_" + S.manageBrandId;
                                    var I = F + "_prism_brand_c_" + S.manageBrandId;
                                    var W = F + "_prism_brand_d_" + S.manageBrandId;
                                    var V = "http://my.yhd.com/favoriteBrand/index.do#brand" + S.manageBrandId;
                                    var O = yhdToolKit.getProductPicByDefaultPic(S.manageBrandLogoUrl, 133, 55);
                                    Q.push("<div class='item_list'>");
                                    Q.push("<div class='brand_logo'>");
                                    Q.push("<a href='" + V + "' data-ref='" + U + "' target='_blank'><img src='" + O + "' /></a>");
                                    Q.push("</div>");
                                    Q.push("<div class='brand_info_tabs'>");
                                    Q.push("<ul class='clearfix'>");
                                    Q.push("<li><a data-ref='" + T + "' href='" + V + "' target='_blank'><p class='num'>" + S.countActivityInfos + "</p><p class='name'>活动</p></a></li>");
                                    Q.push("<li><a data-ref='" + K + "' href='" + V + "' target='_blank'><p class='num'>" + S.countCouponInfos + "</p><p class='name'>优惠券</p></a></li>");
                                    Q.push("<li><a data-ref='" + I + "' href='" + V + "' target='_blank'><p class='num'>" + S.countNewsInfos + "</p><p class='name'>新品</p></a></li>");
                                    Q.push("<li><a data-ref='" + W + "' href='" + V + "' target='_blank'><p class='num'>" + S.countHotsInfos + "</p><p class='name'>热销</p></a></li>");
                                    Q.push("</ul>");
                                    Q.push("</div>");
                                    Q.push("</div>")
                                }
                            }
                        }
                    }
                }
                return Q.join("")
            };
            var x = function(M) {
                if (!M || M.length < 1) {
                    return
                }
                var I = [];
                var K = -1;
                for (var L = 0; L < M.length; L++) {
                    var J = M[L];
                    if (L % 10 == 0) {
                        K = K + 1;
                        if (!I[K]) {
                            I[K] = []
                        }
                        I[K].push(J.pmInfoId)
                    } else {
                        I[K].push(J.pmInfoId)
                    }
                }
                for (var N = 0; N < I.length; N++) {
                    H(I[N])
                }
            };
            var H = function(J) {
                var I = "";
                for (var L = 0; L < J.length; L++) {
                    if (J[L] == "" || J[L] <= 0) {
                        continue
                    }
                    I += "&pmInfoIds=" + J[L]
                }
                if (!I) {
                    return
                }
                var K = "http://interface.yihaodian.com/promotion/search/getPromotionInfoWithSku.do?caller=prism&pointSearch=0&mcsiteId=1&siteType=1&provinceId=" + D + I + "&callback=?";
                jQuery.getJSON(K, function(N) {
                    if (!N) {
                        return true
                    }
                    for (var T = 0; T < N.length; T++) {
                        var S = N[T];
                        if (S == null || S.pmInfoId == null || S.productId == null || !S.isPromotion || !S.promotionInfo || S.promotionInfo.length < 1) {
                            continue
                        }
                        var P = S.promotionInfo[0];
                        var R = a("#prism_fav_pm_" + S.pmInfoId);
                        if (R.length < 1) {
                            continue
                        }
                        var O = "";
                        if (P.type == 2 || P.type == 3) {
                            var M = "http://www.yhd.com/ctg/p/c0-b-a-s1-v0-p1-price-d0-pid" + S.productId + "-pt" + P.promotionId + "-pl" + P.levelId + "-m0";
                            var Q = F + "_prism_fav_pro_l_" + P.promotionId;
                            O = "<h1><i class='icon_bg zhekou'></i><a class='zhekou_link' href='" + M + "' data-ref='" + Q + "' target='_blank'>" + P.promDesc + "</a></h1>"
                        } else {
                            if (P.type == 1 || P.type == 0) {
                                O = "<h1><i class='icon_bg reduction'></i>" + P.promDesc + "</h1>"
                            }
                        }
                        if (O) {
                            R.prepend(O)
                        }
                    }
                    n(a("#proFavList>div.item_box"));
                    l(a("#proFavList"), y)
                })
            }
        };
        var e = function(s, r) {
            s.data("clicked", 1);
            a("div.nav_box", r).hide();
            s.show().find(".info_item").hide().show(500)
        };
        var p = function(s, r) {
            s.hide()
        };
        var l = function(s, r) {
            a("div.scroll-pane", s).jScrollPane()
        };
        var n = function(t) {
            var r = a(window).height();
            var s = t.siblings(".btn_all").length > 1 ? 100 : 100 + 45;
            var v = parseInt(t.parents(".nav_box").css("bottom")) + s;
            var u = r - v;
            if (r < v + t.height()) {
                t.css("height", u)
            }
        }
    })
})(jQuery);
(function(a) {
    a(function() {
        var j = window.loli || (window.loli = {});
        j.app = j.app || {};
        var g = j.app.prism = j.app.prism || {};
        var e = g.functions = g.functions || {};
        var h = e.registerEvent = e.registerEvent || {};
        var c = e.openEvent = e.openEvent || {};
        var i = e.closeEvent = e.closeEvent || {};
        e.registerEvent.account = function(o) {
            var m = a(o.prismLayoutId);
            var k = a(o.prismAccountIconId);
            var q = a(o.prismAccountId);
            var p = o.prismAccountFlag;
            var n = o.pageCode;
            var r = o.prismTrackerPrefix;
            if (!p) {
                k.click(function() {
                });
                return
            }
            k.click(function() {
                if (q.data("dataLoaded") == "1" && q.css("display") != "none") {
                    b(q, m);
                    return
                }
                var s = function(t) {
                    if (t.result == 1) {
                        if (q.data("dataLoaded") == "1") {
                            f(q, m)
                        } else {
                            q.data("dataLoaded", "1");
                            l()
                        }
                    } else {
                        if (yhdPublicLogin) {
                            yhdPublicLogin.showLoginDiv()
                        }
                    }
                };
                j.globalCheckLogin(s);
                if (k.data("clicked") != 1) {
                    gotracker("2", r + "_prism_account");
                    k.data("clicked", 1)
                }
            });
            var l = function() {
                var t = function(I) {
                    if (!I) {
                        return
                    }
                    var K = I.endUserCredit ? I.endUserCredit : 0;
                    var G = I.exp ? I.exp : 0;
                    var u = I.nextGradeExpNeed;
                    var B = 0;
                    var A = o.prismAccountFaceImg;
                    var x = "";
                    var F = "http://my.yhd.com";
                    var z = "http://vip.yhd.com";
                    var H = "http://my.yhd.com/points/displayPointAccount.do";
                    var C = r + "_prism_account_my";
                    var M = r + "_prism_account_grade";
                    var D = r + "_prism_account_grade_val";
                    var v = r + "_prism_account_grade_bar";
                    var w = r + "_prism_account_grade_credit";
                    var E = q.data("accountSimpleData");
                    if (E) {
                        B = E.memberGrade;
                        if (E.endUserPic) {
                            A = E.endUserPic
                        }
                        x = E.endUserName
                    }
                    if (B == null || B < 0 || B > 3) {
                        B = 0
                    }
                    var J = 0;
                    var L = "";
                    if (G < 1) {
                        J = 0;
                        L = "再累计1成长值可升级为V1"
                    } else {
                        if (G < 1000) {
                            J = G / 10;
                            L = "再累计" + (u ? u : (1000 - G)).toString() + "成长值可升级为V2"
                        } else {
                            if (G < 3000) {
                                J = G / 30;
                                L = "再累计" + (u ? u : (3000 - G)).toString() + "成长值可升级为V3"
                            } else {
                                J = G >= 10000 ? 75 : G / 10000 * 0.75 * 100;
                                L = "您已经是最高级别"
                            }
                        }
                    }
                    var y = [];
                    y.push('<div class="mod_prism_usercenter info_item">');
                    y.push('<div class="avata_box"><a href="' + F + '" data-ref="' + C + '" target="_blank"><img src="' + A + '" /></a></div>');
                    y.push('<a href="javascript:;" class="icon_bg face_btn_close" title="关闭"></a>');
                    y.push('<p class="user_name"><a href="' + z + '" data-ref="' + M + '" target="_blank"><i class="icon_bg vip_icon vip_icon' + B + '"></i></a><a href="' + F + '" data-ref="' + C + '" target="_blank">' + x + "</a>&nbsp;</p>");
                    y.push('<div class="vip_progress_box">');
                    y.push('<ul class="clearfix">');
                    y.push("<li>");
                    y.push("<p>成长值</p>");
                    y.push('<p class="growth_value"><a data-ref="' + D + '" target="_blank" href="' + z + '">' + G + "</a></p>");
                    y.push("</li>");
                    y.push('<li class="points_box">');
                    y.push("<p>积分</p>");
                    y.push('<p class="growth_value"><a data-ref="' + w + '" target="_blank" href="' + H + '">' + K + "</a></p>");
                    y.push("</li>");
                    y.push("</ul>");
                    y.push('<div class="growth_progress_bar icon_bg"><p class="icon_bg progress_bar" style="width:' + J + '%"><i class="icon_bg"></i></p></div>');
                    y.push('<p class="growth_tips">' + L + "</p>");
                    y.push('<a href="' + z + '" data-ref="' + v + '" target="_blank" class="blue_link view_growth">查看我的进度</a>');
                    y.push("</div>");
                    y.push("</div>");
                    q.append(y.join(""));
                    f(q, m);
                    a("a.face_btn_close", q).click(function() {
                        b(q, m)
                    })
                };
                var s = URLPrefix.central + "/homepage/ajaxFindPrismMemberUserInfo.do?callback=?";
                a.getJSON(s, function(u) {
                    var w = u;
                    if (w) {
                        if (w.status == 1) {
                            var v = w.userInfo;
                            t(v)
                        } else {
                            if (w.status == 0) {
                                yhdPublicLogin.showLoginDiv()
                            }
                        }
                    }
                })
            }
        };
        e.openEvent.account = function(k) {
            var n = a(k.prismAccountIconId);
            var m = a(k.prismAccountId);
            var l = k.prismAccountFlag;
            if (!l) {
                return
            }
            if (!m.data("numsLoaded")) {
                d(k, n, m);
                m.data("numsLoaded", "1");
                recordTrackInfoWithType("1", k.prismTrackerPrefix + "_prism_account_show")
            }
        };
        e.closeEvent.account = function(k) {
            var n = a(k.prismLayoutId);
            var m = a(k.prismAccountId);
            var l = k.prismAccountFlag;
            if (!l) {
                return
            }
            b(m, n)
        };
        var d = function(n, l, k) {
            var m = URLPrefix.central + "/homepage/ajaxFindPrismSimpleMemberUserInfo.do?callback=?";
            var o = function(p) {
                k.data("accountSimpleData", p);
                if (!p) {
                    return
                }
                var q = p.memberGrade;
                var r = p.endUserPic;
                if (q == null || q < 0 || q > 3) {
                    q = 0
                }
                if (r) {
                    a("img.face_img", l).attr("src", r)
                }
                if (p.endUserName) {
                    l.append('<span class="icon_bg face_tips"><i class="icon_bg v' + q + '"></i>' + p.endUserName + "</span>")
                }
                a("span", l).show(500)
            };
            a.getJSON(m, function(p) {
                var r = p;
                if (r) {
                    if (r.status == 1) {
                        var q = r.userInfo;
                        o(q)
                    }
                }
            })
        };
        var f = function(l, k) {
            l.data("clicked", 1);
            a("div.nav_box", k).hide();
            l.show().find(".info_item").hide().show(300)
        };
        var b = function(l, k) {
            l.hide();
            a("#newPrismAccountIcon").removeClass("nav_face_select")
        }
    })
})(jQuery);
(function(a) {
    a(function() {
        var c = window.loli || (window.loli = {});
        c.app = c.app || {};
        var g = c.app.prism = c.app.prism || {};
        var f = g.functions = g.functions || {};
        var b = f.registerEvent = f.registerEvent || {};
        var d = f.openEvent = f.openEvent || {};
        var e = f.closeEvent = f.closeEvent || {};
        f.registerEvent.msgs = function(k) {
            var p = a(k.prismLayoutId);
            var i = a(k.prismMsgsIconId);
            var o = a(k.prismMsgsId);
            var q = k.prismMsgsFlag;
            var u = k.pageCode;
            var j = k.prismTrackerPrefix;
            i.click(function() {
                if (o.data("dataLoaded") == "1" && o.css("display") != "none") {
                    n(o, p);
                    return
                }
                if (o.data("dataLoaded") == "1") {
                    r(o, p)
                } else {
                    o.data("dataLoaded", "1");
                    var x = o.data("messageNumsData");
                    var w = m(x);
                    var v = h(x);
                    o.append(w);
                    a("#prismMessageList>div.item_box").append(v);
                    r(o, p);
                    l(a("#prismMessageList>div.item_box"));
                    t("#prismMessageList", p);
                    a("a.item_btn_close", o).click(function() {
                        n(o, p)
                    })
                }
                if (i.data("clicked") != 1) {
                    gotracker("2", j + "_prism_msg");
                    i.data("clicked", 1)
                }
            });
            var m = function(w) {
                var x = 0;
                if (w != null && w.dataSize != null) {
                    x = w.dataSize
                }
                var v = [];
                v.push("<div class='info_item info_box'>");
                v.push("<div class='tabs_box'>");
                v.push("<ul class='clearfix'>");
                if (x > 0) {
                    v.push("<li class='cur'>消息中心 " + (x > 999 ? "999+" : x) + "</li>")
                } else {
                    v.push("<li class='cur'>消息中心</li>")
                }
                v.push("</ul>");
                v.push("</div>");
                v.push("<div class='tabs_list'  id='prismMessageList'>");
                v.push("<div class='item_box scroll-pane'>");
                v.push("</div>");
                v.push("</div>");
                v.push("<a href='javascript:;' class='icon_bg item_btn_close'>close</a>");
                return v.join("")
            };
            var h = function(x) {
                var v;
                if (x != null && x.list != null) {
                    v = x.list
                }
                var w = [];
                if (!q) {
                    w.push("<div class='item_empty'>");
                    w.push("<p><i class='icon_bg empty_news'></i></p>");
                    w.push("<p>此功能尚未开放!</p>");
                    w.push("</div>");
                    return w.join("")
                }
                if (v && v.length > 0) {
                    w.push("<div class='item_list'>");
                    w.push("<h1><span>根据您的浏览为您推荐促销活动</span></h1>");
                    a.each(v, function() {
                        var y = this.id;
                        var z = this.promotionLevel;
                        var A = j + "_prism_msg_a_" + y;
                        w.push("<span class='manjian'><a  data-ref=" + A + " href=" + this.linkUrl + " target='_blank'>" + this.title + "</></span>");
                        var B = this.productList;
                        if (B && B.length > 0) {
                            w.push("<div class='pro_box'><div class='pro_pic'><ul class='clearfix'>");
                            a.each(B, function(G) {
                                if (B[G]) {
                                    var D = B[G].picUrl;
                                    if (D) {
                                        var E = D.lastIndexOf("_");
                                        if (E && E > 0) {
                                            D = D.substring(0, E) + "_40x40.jpg"
                                        }
                                    }
                                    var F = B[G].productId;
                                    var H = URLPrefix.central + "/ctg/p/c0-b-a-s1-v0-p1-price0-d0-pid" + F + "-pt" + y + "-pl" + z + "-m0";
                                    var C = j + "_prism_msg_a_" + y + "_" + B[G].pmId;
                                    w.push("<li><a  data-ref=" + C + " href=" + H + " target='_blank'><img src=" + D + " /></a></li>")
                                }
                            });
                            w.push("</ul></div></div>")
                        }
                    });
                    w.push("</div>")
                } else {
                    w.push("<div class='item_empty'>");
                    w.push("<p><i class='icon_bg empty_news'></i></p>");
                    w.push("<p>您还没有消息哦~</p>");
                    w.push("</div>")
                }
                return w.join("")
            };
            f.openEvent.msgs = function(y) {
                var x = a(y.prismMsgsIconId);
                var v = a(y.prismMsgsId);
                var w = y.prismMsgsFlag;
                if (!w) {
                    return
                }
                if (!v.data("numsLoaded")) {
                    s(y, x, v);
                    v.data("numsLoaded", "1");
                    recordTrackInfoWithType("1", y.prismTrackerPrefix + "_prism_msg_show")
                }
            };
            f.closeEvent.msgs = function(y) {
                var x = a(y.prismLayoutId);
                var v = a(y.prismMsgsId);
                var w = y.prismMsgsFlag;
                if (!w) {
                    return
                }
                n(v, x)
            };
            var s = function(x, A, y) {
                var v = URLPrefix.pms + "/interface/prism.do?callback=?";
                var z = function(B) {
                    y.data("messageNumsData", B);
                    if (B && B.dataSize > 0) {
                        A.html("<span class='icon_bg bubble_tips'>" + B.dataSize + "</span>")
                    }
                    a("span", A).show(500)
                };
                var w = {userid: x.userId,currSiteId: x.currSiteId,currSiteType: x.currSiteType,provinceId: x.provinceId,pageCode: x.pageCode,isPromotionNew: 1,prismType: 191,prodNumPerPromotion: 4};
                a.getJSON(v, w, function(B) {
                    var C = B;
                    if (C) {
                        if (C.success == 1) {
                            if (C.value) {
                                var B = C.value[0];
                                if (B && B.status == 1) {
                                    z(B)
                                }
                            }
                        }
                    }
                })
            };
            var r = function(v, w) {
                v.data("clicked", 1);
                a("div.nav_box", w).hide();
                v.show().find(".info_item").hide().show(500)
            };
            var n = function(v, w) {
                v.hide()
            };
            var t = function(v, w) {
                a("div.scroll-pane", v).jScrollPane()
            };
            var l = function(w) {
                var z = a(window).height();
                var v = w.siblings(".btn_all").length > 1 ? 100 : 100 + 45;
                var y = parseInt(w.parents(".nav_box").css("bottom")) + v;
                var x = z - y;
                if (z < y + w.height()) {
                    w.css("height", x)
                }
            }
        }
    })
})(jQuery);
var killAutoCloseGlobalPrismTimeout = null;
(function(a) {
    a(function() {
        var j = YHDOBJECT.globalVariable().globalPageCode;
        var n = a.cookie("provinceId");
        var d = a.cookie("yihaodian_uid");
        var l = (typeof globalNewPrismFlag == "undefined" || globalNewPrismFlag == "0") ? 0 : 1;
        var m = (typeof globalNewPrismAccountFlag != "undefined" && globalNewPrismAccountFlag == "0") ? 0 : 1;
        var s = (typeof globalNewPrismOrderFlag != "undefined" && globalNewPrismOrderFlag == "0") ? 0 : 1;
        var h = (typeof globalNewPrismCouponFlag != "undefined" && globalNewPrismCouponFlag == "0") ? 0 : 1;
        var c = (typeof globalNewPrismFavoriteFlag != "undefined" && globalNewPrismFavoriteFlag == "0") ? 0 : 1;
        var f = (typeof globalNewPrismMsgsFlag != "undefined" && globalNewPrismMsgsFlag == "0") ? 0 : 1;
        var e = (typeof globalHideNewPrismPage != "undefined") ? globalHideNewPrismPage : "";
        if ((typeof (j) == "undefined" || j == -1) || !n || !d || !l) {
            return
        }
        var q = window.navigator.userAgent.toLowerCase();
        var r = /msie ([\d.]+)/;
        if (r.test(q)) {
            var g = parseInt(r.exec(q)[1]);
            if (g <= 6) {
                return
            }
        }
        if (e.indexOf(j) != -1) {
            return
        }
        var i = a("#isYiHaoDian").val();
        if ("0" == i && "YHD_ITEM" == j) {
            return
        }
        var t = {currSiteId: (typeof currSiteId == "undefined") ? 1 : currSiteId,currSiteType: 1,provinceId: n,pageCode: j,userId: d,prismFlag: l,prismAccountFlag: m,prismOrderFlag: s,prismCouponFlag: h,prismFavoriteFlag: c,prismMsgsFlag: f,hideNewPrismPage: e,prismLayoutId: "#newPrismLayout",prismBtnId: "#newPrismBtn",prismAccountIconId: "#newPrismAccountIcon",prismOrderIconId: "#newPrismOrderIcon",prismCouponIconId: "#newPrismCouponIcon",prismFavoriteIconId: "#newPrismFavoriteIcon",prismMsgsIconId: "#newPrismMsgsIcon",prismAccountId: "#newPrismAccount",prismOrderId: "#newPrismOrder",prismCouponId: "#newPrismCoupon",prismFavoriteId: "#newPrismFavorite",prismMsgsId: "#newPrismMsgs",prismAccountFaceImg: URLPrefix.statics + "/global/images/prism_new/peopleicon.png",prismTrackerPrefix: "global"};
        var b = window.loli || (window.loli = {});
        b.app = b.app || {};
        var v = b.app.prism = b.app.prism || {};
        var p = v.functions = v.functions || {};
        var k = p.registerEvent = p.registerEvent || {};
        var u = p.openEvent = p.openEvent || {};
        var o = p.closeEvent = p.closeEvent || {};
        v.functions.main = function(B) {
            var A = function() {
                var D = z(B);
                a("body").append(D);
                recordTrackInfoWithType("1", B.prismTrackerPrefix + "_prism_show");
                w()
            };
            var w = function() {
                var J = a(B.prismLayoutId);
                var I = a(B.prismBtnId);
                var H = J;
                var D = [{e: H.find(".nav_news"),x: -194,y: -42,selectClass: "nav_news_select",box: H.find(".news_box")}, {e: H.find(".nav_gift"),x: -183,y: -96,selectClass: "nav_gift_select",box: H.find(".gift_box")}, {e: H.find(".nav_face"),x: -158,y: -158,selectClass: "nav_face_select",box: H.find(".face_box")}, {e: H.find(".nav_orders"),x: -96,y: -183,selectClass: "nav_orders_select",box: H.find(".orders_box")}, {e: H.find(".nav_favorite"),x: -42,y: -194,selectClass: "nav_favorite_select",box: H.find(".favorite_box")}];
                I.click(function() {
                    J.clicked = !J.clicked;
                    if (J.clicked) {
                        a(this).addClass("switch_button_select")
                    } else {
                        a(this).removeClass("switch_button_select")
                    }
                    G();
                    F(J.clicked);
                    if (J.clicked) {
                        C()
                    } else {
                        x()
                    }
                    if (I.data("clicked") != 1) {
                        gotracker("2", B.prismTrackerPrefix + "_prism_switch");
                        I.data("clicked", 1)
                    }
                });
                var F = function(N) {
                    var L = 200;
                    for (var M = 0; M < D.length; M++) {
                        var K = D[M];
                        if (N) {
                            K.e.stop().animate({left: K.x,top: K.y,opacity: 1}, L, function() {
                                a(this).find("span").show(500)
                            }).addClass("rotate360")
                        } else {
                            K.e.stop().animate({left: -47,top: -47,opacity: 0}, L).removeClass("rotate360").find("span").hide();
                            E()
                        }
                        L += 80
                    }
                };
                var G = function() {
                    for (var K = 0; K < D.length; K++) {
                        var L = D[K];
                        L.e[0].index = K;
                        L.e.bind("click", function(O) {
                            if (killAutoCloseGlobalPrismTimeout) {
                                clearTimeout(killAutoCloseGlobalPrismTimeout)
                            }
                            var P = a(this);
                            var N = D[this.index].selectClass;
                            if (!P.hasClass(N)) {
                                E();
                                if (N == "nav_face_select") {
                                    if (m) {
                                        var M = function(Q) {
                                            if (Q.result == 1) {
                                                P.addClass(N)
                                            }
                                        };
                                        b.globalCheckLogin(M)
                                    } else {
                                    }
                                } else {
                                    P.addClass(N)
                                }
                            } else {
                                P.removeClass(N)
                            }
                        })
                    }
                };
                var E = function() {
                    for (var K = 0; K < D.length; K++) {
                        D[K].e.removeClass(D[K].selectClass)
                    }
                };
                y()
            };
            var y = function() {
                var H = v.functions.registerEvent.account || function() {
                };
                var F = v.functions.registerEvent.order || function() {
                };
                var E = v.functions.registerEvent.coupon || function() {
                };
                var G = v.functions.registerEvent.favorite || function() {
                };
                var D = v.functions.registerEvent.msgs || function() {
                };
                H(B);
                F(B);
                E(B);
                G(B);
                D(B)
            };
            var C = function() {
                var H = v.functions.openEvent.account || function() {
                };
                var F = v.functions.openEvent.order || function() {
                };
                var E = v.functions.openEvent.coupon || function() {
                };
                var G = v.functions.openEvent.favorite || function() {
                };
                var D = v.functions.openEvent.msgs || function() {
                };
                H(B);
                F(B);
                E(B);
                G(B);
                D(B)
            };
            var x = function() {
                var H = v.functions.closeEvent.account || function() {
                };
                var F = v.functions.closeEvent.order || function() {
                };
                var E = v.functions.closeEvent.coupon || function() {
                };
                var G = v.functions.closeEvent.favorite || function() {
                };
                var D = v.functions.closeEvent.msgs || function() {
                };
                H(B);
                F(B);
                E(B);
                G(B);
                D(B)
            };
            var z = function(D) {
                var E = [];
                E.push("<div class='mod_lengjing' id='newPrismLayout'>");
                E.push("<div id='newPrismMsgs' class='nav_box news_box' style='display:none;'></div>");
                E.push("<div id='newPrismCoupon' class='nav_box gift_box' style='display:none;'></div>");
                E.push("<div id='newPrismAccount' class='nav_box face_box' style='display:none;'></div>");
                E.push("<div id='newPrismOrder' class='nav_box orders_box' style='display:none;'></div>");
                E.push("<div id='newPrismFavorite' class='nav_box favorite_box' style='display:none;'></div>");
                E.push("<a id='newPrismMsgsIcon' href='javascript:;' class='icon_bg nav_news'></a>");
                E.push("<a id='newPrismCouponIcon' href='javascript:;' class='icon_bg nav_gift'></a>");
                E.push("<a id='newPrismAccountIcon' href='javascript:;' class='nav_face'>");
                E.push("<img class='face_img' src='" + D.prismAccountFaceImg + "' width='48' height='48' />");
                E.push("</a>");
                E.push("<a id='newPrismOrderIcon' href='javascript:;' class='icon_bg nav_orders'></a>");
                E.push("<a id='newPrismFavoriteIcon' href='javascript:;' class='icon_bg nav_favorite'></a>");
                E.push("<a id='newPrismBtn' href='javascript:;' class='icon_bg switch_button'></a>");
                E.push("</div>");
                return E.join("")
            };
            A()
        };
        v.functions.autoOpen = function(z) {
            try {
            	ilog("prism_42420486...");
                var w = "prism_" + d;
                if (jQuery.cookie(w)) {
                    return
                }
                var y = a(z.prismBtnId);
                jQuery.cookie(w, 1, {domain: no3wUrl,path: "/",expires: 90});
                y.click();
                killAutoCloseGlobalPrismTimeout = setTimeout(function() {
                    if (y.hasClass("switch_button_select")) {
                        y.click()
                    }
                }, 1000 * 3)
            } catch (x) {
            }
        };
        v.functions.showStar = function(A, z) {
            if (A == null || a(A).size() == 0) {
                return
            }
            a(t.prismLayoutId).find("div.nav_box").hide();
            var y = a(t.prismFavoriteIconId).get(0);
            if ("account" == z) {
                y = a(t.prismAccountIconId).get(0)
            } else {
                if ("order" == z) {
                    y = a(t.prismOrderIconId).get(0)
                } else {
                    if ("coupon" == z) {
                        y = a(t.prismCouponIconId).get(0)
                    } else {
                        if ("favorite" == z) {
                            y = a(t.prismFavoriteIconId).get(0)
                        } else {
                            if ("msgs" == z) {
                                y = a(t.prismMsgsIconId).get(0)
                            }
                        }
                    }
                }
            }
            var B = document.createElement("span");
            B.className = "attention_star";
            B.id = "attention_star";
            var w = a(A).offset().left;
            var x = a(A).offset().top;
            a(B).appendTo(a("html body")).css({left: w,top: x});
            funParabola(B, y, {speed: 200,complete: function() {
                    var C = parseInt(a(B).css("top")) - 20;
                    a(B).delay(300).animate({top: C - 20,opacity: 0}, 1000, function() {
                        a(this).remove()
                    })
                }}).mark().init()
        };
        v.functions.reset = function(A) {
            var z = v.functions.resetEvent.account || function() {
            };
            var w = v.functions.resetEvent.order || function() {
            };
            var y = v.functions.resetEvent.coupon || function() {
            };
            var x = v.functions.resetEvent.favorite || function() {
            };
            var B = v.functions.resetEvent.msgs || function() {
            };
            if ("account" == A) {
                z(t)
            } else {
                if ("order" == A) {
                    w(t)
                } else {
                    if ("coupon" == A) {
                        y(t)
                    } else {
                        if ("favorite" == A) {
                            x(t)
                        } else {
                            if ("msgs" == A) {
                                B(t)
                            } else {
                                z(t);
                                w(t);
                                y(t);
                                x(t);
                                B(t)
                            }
                        }
                    }
                }
            }
        };
        window.showGlobalPrismTimeoutHandler = setTimeout(function() {
            b.app.prism.functions.main(t);
            b.app.prism.functions.autoOpen(t)
        }, 1000 * 3)
    })
})(jQuery);
function showGlobalNewPrismStar(c, b) {
    var a = window.loli || (window.loli = {});
    if (!a.app || !a.app.prism || !a.app.prism.functions || !a.app.prism.functions.showStar || !a.app.prism.functions.reset) {
        return
    }
    a.app.prism.functions.showStar(c, b);
    a.app.prism.functions.reset(b)
}
;

ilog('document.cookie:'+document.cookie);