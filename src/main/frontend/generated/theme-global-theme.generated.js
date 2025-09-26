import 'construct-style-sheets-polyfill';
import { injectGlobalCss } from 'Frontend/generated/jar-resources/theme-util.js';
import { webcomponentGlobalCssInjector } from 'Frontend/generated/jar-resources/theme-util.js';
import './theme-global-theme.components.generated.js';
let needsReloadOnChanges = false;
import { badge } from '@vaadin/vaadin-lumo-styles/badge.js';
import stylesCss from 'themes/global-theme/styles.css?inline';

  let themeRemovers = new WeakMap();
  let targets = [];

  export const applyTheme = (target) => {
    const removers = [];
    if (target !== document) {
      removers.push(injectGlobalCss(badge.cssText, '', target, true));
removers.push(injectGlobalCss(stylesCss.toString(), '', target));
    
      
    }
    
    

    if (import.meta.hot) {
      targets.push(new WeakRef(target));
      themeRemovers.set(target, removers);
    }

  }


if (import.meta.hot) {
  import.meta.hot.accept((module) => {

    if (needsReloadOnChanges) {
      window.location.reload();
    } else {
      targets.forEach(targetRef => {
        const target = targetRef.deref();
        if (target) {
          themeRemovers.get(target).forEach(remover => remover())
          module.applyTheme(target);
        }
      })
    }
  });

  import.meta.hot.on('vite:afterUpdate', (update) => {
    document.dispatchEvent(new CustomEvent('vaadin-theme-updated', { detail: update }));
  });
}

