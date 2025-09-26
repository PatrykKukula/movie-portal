import { injectGlobalWebcomponentCss } from 'Frontend/generated/jar-resources/theme-util.js';

import { injectGlobalCss } from 'Frontend/generated/jar-resources/theme-util.js';

import { css, unsafeCSS, registerStyles } from '@vaadin/vaadin-themable-mixin';
import $cssFromFile_0 from 'Frontend/styles/account-styles.css?inline';
import $cssFromFile_1 from 'Frontend/styles/common-styles.css?inline';
import $cssFromFile_2 from 'Frontend/styles/main-layout.css?inline';
import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/combo-box/theme/lumo/vaadin-combo-box.js';
import 'Frontend/generated/jar-resources/flow-component-renderer.js';
import 'Frontend/generated/jar-resources/comboBoxConnector.js';
import '@vaadin/text-field/theme/lumo/vaadin-text-field.js';
import '@vaadin/icons/vaadin-iconset.js';
import '@vaadin/form-layout/theme/lumo/vaadin-form-layout.js';
import '@vaadin/dialog/theme/lumo/vaadin-dialog.js';
import '@vaadin/login/theme/lumo/vaadin-login-form.js';
import '@vaadin/text-area/theme/lumo/vaadin-text-area.js';
import '@vaadin/password-field/theme/lumo/vaadin-password-field.js';
import '@vaadin/vertical-layout/theme/lumo/vaadin-vertical-layout.js';
import '@vaadin/tooltip/theme/lumo/vaadin-tooltip.js';
import '@vaadin/icon/theme/lumo/vaadin-icon.js';
import '@vaadin/upload/theme/lumo/vaadin-upload.js';
import '@vaadin/form-layout/theme/lumo/vaadin-form-item.js';
import '@vaadin/horizontal-layout/theme/lumo/vaadin-horizontal-layout.js';
import '@vaadin/multi-select-combo-box/theme/lumo/vaadin-multi-select-combo-box.js';
import '@vaadin/button/theme/lumo/vaadin-button.js';
import 'Frontend/generated/jar-resources/disableOnClickFunctions.js';
import '@vaadin/avatar/theme/lumo/vaadin-avatar.js';
import '@vaadin/notification/theme/lumo/vaadin-notification.js';
import 'Frontend/generated/jar-resources/vaadin-grid-flow-selection-column.js';
import '@vaadin/grid/theme/lumo/vaadin-grid-column.js';
import '@vaadin/app-layout/theme/lumo/vaadin-app-layout.js';
import '@vaadin/context-menu/theme/lumo/vaadin-context-menu.js';
import 'Frontend/generated/jar-resources/contextMenuConnector.js';
import 'Frontend/generated/jar-resources/contextMenuTargetConnector.js';
import '@vaadin/grid/theme/lumo/vaadin-grid.js';
import '@vaadin/grid/theme/lumo/vaadin-grid-sorter.js';
import '@vaadin/checkbox/theme/lumo/vaadin-checkbox.js';
import 'Frontend/generated/jar-resources/gridConnector.ts';
import '@vaadin/grid/theme/lumo/vaadin-grid-column-group.js';
import 'Frontend/generated/jar-resources/lit-renderer.ts';
import '@vaadin/confirm-dialog/theme/lumo/vaadin-confirm-dialog.js';
import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';
import 'Frontend/generated/jar-resources/ReactRouterOutletElement.tsx';

injectGlobalCss($cssFromFile_0.toString(), 'CSSImport end', document);
injectGlobalWebcomponentCss($cssFromFile_0.toString());

injectGlobalCss($cssFromFile_1.toString(), 'CSSImport end', document);
injectGlobalWebcomponentCss($cssFromFile_1.toString());

injectGlobalCss($cssFromFile_2.toString(), 'CSSImport end', document);
injectGlobalWebcomponentCss($cssFromFile_2.toString());

const loadOnDemand = (key) => {
  const pending = [];
  if (key === '0075c6c441a1d650f8ff9fa891b2660ae1b8a46ec80ec6ee3a5a2068c99f86cf') {
    pending.push(import('./chunks/chunk-6b26ee68d5555f6386e8b4b97ddb9d479a2f16c104eda2054a182271a216cfb3.js'));
  }
  if (key === '69a5dd8ef4e55b520dbc0c6404118178ca12a0541022cc506fac5ea5c4f8454e') {
    pending.push(import('./chunks/chunk-01de0b6159397712b18f61163c997aabadc38decbfd6a5ad686383e988d4ddf3.js'));
  }
  if (key === 'ede752640109a10b0960a025d08f113a9e60fb73e29a3935e00cb502fc62052c') {
    pending.push(import('./chunks/chunk-c239a10969acd74b1e8bb422e6fd89c6ce6d8d8b8e89064242adbd702bb664d2.js'));
  }
  if (key === '2a1eedcdc79ec8305513096082fd710db5b1c0c497a60fcf3f155c1f7636c2b9') {
    pending.push(import('./chunks/chunk-336ba760a740e620ea3eab3a7d472958704ed5b3ec3b6e4428fede4105862cec.js'));
  }
  if (key === '23be5083764fd0b1804e637cf09d337c2b56962c8b325890decd759e4efe94db') {
    pending.push(import('./chunks/chunk-e2d7a817b91ec181ea1010d6bf650100354608732271187ba986eee27e782ba7.js'));
  }
  if (key === 'f8b590647ef8098b25ceeab3c75c84f4c0400a73c6731904a300401925c9791e') {
    pending.push(import('./chunks/chunk-b0a6134d253df51e762cfee930c5a5379d8392cf5c70935f4ee1f0168cfbc7e4.js'));
  }
  if (key === '195c5bc8687011c924ca84b2af8f554e92fd1c50fc0a7b835bb956ae27813d5b') {
    pending.push(import('./chunks/chunk-b0a6134d253df51e762cfee930c5a5379d8392cf5c70935f4ee1f0168cfbc7e4.js'));
  }
  if (key === '7266df6b04225e6030ede5fa0dfe18a231808964a7c75fe84089ea85485ff894') {
    pending.push(import('./chunks/chunk-42e78c17d74a0f261103e087ada2b0d4ff203cf11593c5521403a6ee18298f94.js'));
  }
  if (key === '866255447d6603bf924facd300aabc90286bd834c1342668b0321480dd68630f') {
    pending.push(import('./chunks/chunk-e2d7a817b91ec181ea1010d6bf650100354608732271187ba986eee27e782ba7.js'));
  }
  if (key === 'b502fd116b9a8a5495b1d855dedabbc51596187d67642129af4a31c4945da827') {
    pending.push(import('./chunks/chunk-adcafe536b4fca39c4c2a1a6c668f7d30c74703e66e46fcf24709efdd7d48eb0.js'));
  }
  if (key === '35e4c3f988a00e132830a68d7e12f31c4801436a2f2cbe6e61f9175561b8c1b2') {
    pending.push(import('./chunks/chunk-e2d7a817b91ec181ea1010d6bf650100354608732271187ba986eee27e782ba7.js'));
  }
  if (key === '4cb3a181dbbecec5d4dc64209bdc1ef1cbfc3ede9035bcd875b5dc34cff94962') {
    pending.push(import('./chunks/chunk-2577fcb02547ae96a08debfccbd2bc6704b7af54032bdc1e446f85ba0c892f12.js'));
  }
  if (key === 'c4c517f0f70e0ffc3c00c9358ddb6a43b782c957ad17d603b6c88251601cb487') {
    pending.push(import('./chunks/chunk-e2d7a817b91ec181ea1010d6bf650100354608732271187ba986eee27e782ba7.js'));
  }
  if (key === '766d49423f36fa8d5b92a8864e7490c44b93a551cf682f2bd44ab0a740f5207f') {
    pending.push(import('./chunks/chunk-e2d7a817b91ec181ea1010d6bf650100354608732271187ba986eee27e782ba7.js'));
  }
  if (key === 'e0bdf3c7ca27939f41f087bdf81d9eda1c3703df9563b363f69f8834b114c536') {
    pending.push(import('./chunks/chunk-6b26ee68d5555f6386e8b4b97ddb9d479a2f16c104eda2054a182271a216cfb3.js'));
  }
  if (key === 'dd27beb1863c7cb4ce73f3bc672ec11df2194a6e01f2de6a1d8f52eaf6e7fc7c') {
    pending.push(import('./chunks/chunk-2b91a4949897cdc02d3b89a5087027beebb13e966e7f3ac0c28d6d55fc61c317.js'));
  }
  if (key === '75aef337485d6c4cdcf619b5faf4b1877484b3f737a610b1c2156dd6f4fd48ed') {
    pending.push(import('./chunks/chunk-3974a8330011ac1bfd323c9956dfdafe423a7a607fa18465f667c6774b0adf60.js'));
  }
  if (key === '0d1214ed8f47f510a17f5da7adb2147412c625461c6b58d8715933c40174d0c8') {
    pending.push(import('./chunks/chunk-42081a24fc025e97dbf3b84c5ae458c5a015762424b802aed33885ed718741f8.js'));
  }
  if (key === 'eeb85872c8b5005a0794f446423f5491a3353e272aead2627b7110f0f41e5493') {
    pending.push(import('./chunks/chunk-0357d303e1ed8fc147016c9de65975f4ce490557b69d3e368784bf27e387f198.js'));
  }
  if (key === 'b17a7422f5927a9109ac31c0c38d6378cfc56e71bff9a983ec64e990744a93a4') {
    pending.push(import('./chunks/chunk-e2d7a817b91ec181ea1010d6bf650100354608732271187ba986eee27e782ba7.js'));
  }
  if (key === '917e871f4df4b67d7f414fb3cbef44d678e157fd4438e2fe8a284d7f0fcbf02f') {
    pending.push(import('./chunks/chunk-2b91a4949897cdc02d3b89a5087027beebb13e966e7f3ac0c28d6d55fc61c317.js'));
  }
  if (key === '6da5edfa91bcfe674f215b73ef63f363054d9a7197737fc36902a998f6781a43') {
    pending.push(import('./chunks/chunk-6b26ee68d5555f6386e8b4b97ddb9d479a2f16c104eda2054a182271a216cfb3.js'));
  }
  if (key === '1c20c4c614db3b2d4a6435f7d52e85a03fd8c6ba818146cbb2607177618797a4') {
    pending.push(import('./chunks/chunk-c0284438b7561ec85f3fa0ea290cf9781b089eb6ee19a2a8c828fbf4a14430b3.js'));
  }
  if (key === 'abe4121d3884115c9cdf2b17733a107d9a71968b822e6b0eaac0b93b703f90d4') {
    pending.push(import('./chunks/chunk-ae0f1f7aa36383b5f20e2a61724660548b84ebf3784d6f8948d69eb6314e8b01.js'));
  }
  if (key === 'c5a0c8609b46c9c233b3a1b756ed22d744c7bb1c44e91e180584522fde62d21c') {
    pending.push(import('./chunks/chunk-248cb81fb1e95ae45374aae4863455bdb9109093848ef425692e112125a8a4b6.js'));
  }
  if (key === 'fe7830e64fbbca49be30cf9edd8f25351d72255032263565e28e9dce3cc005d3') {
    pending.push(import('./chunks/chunk-e2d7a817b91ec181ea1010d6bf650100354608732271187ba986eee27e782ba7.js'));
  }
  return Promise.all(pending);
}

window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};
window.Vaadin.Flow.loadOnDemand = loadOnDemand;
window.Vaadin.Flow.resetFocus = () => {
 let ae=document.activeElement;
 while(ae&&ae.shadowRoot) ae = ae.shadowRoot.activeElement;
 return !ae || ae.blur() || ae.focus() || true;
}