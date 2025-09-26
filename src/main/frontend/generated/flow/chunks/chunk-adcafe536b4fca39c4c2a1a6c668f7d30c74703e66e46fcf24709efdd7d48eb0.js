import { injectGlobalCss } from 'Frontend/generated/jar-resources/theme-util.js';

import { css, unsafeCSS, registerStyles } from '@vaadin/vaadin-themable-mixin';
import $cssFromFile_0 from 'Frontend/styles/error-styles.css?inline';

injectGlobalCss($cssFromFile_0.toString(), 'CSSImport end', document);