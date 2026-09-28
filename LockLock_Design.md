---
name: Serene Vault
colors:
  surface: '#fbf9fa'
  surface-dim: '#dcd9da'
  surface-bright: '#fbf9fa'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f6f3f4'
  surface-container: '#f0edee'
  surface-container-high: '#eae7e8'
  surface-container-highest: '#e4e2e3'
  on-surface: '#1b1b1c'
  on-surface-variant: '#44474c'
  inverse-surface: '#303031'
  inverse-on-surface: '#f3f0f1'
  outline: '#75777c'
  outline-variant: '#c5c6cc'
  surface-tint: '#555f6d'
  primary: '#343e4b'
  on-primary: '#ffffff'
  primary-container: '#4b5563'
  on-primary-container: '#bfcada'
  inverse-primary: '#bdc7d8'
  secondary: '#505f76'
  on-secondary: '#ffffff'
  secondary-container: '#d0e1fb'
  on-secondary-container: '#54647a'
  tertiary: '#21433d'
  on-tertiary: '#ffffff'
  tertiary-container: '#395a54'
  on-tertiary-container: '#acd0c8'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#d9e3f4'
  primary-fixed-dim: '#bdc7d8'
  on-primary-fixed: '#121c28'
  on-primary-fixed-variant: '#3e4755'
  secondary-fixed: '#d3e4fe'
  secondary-fixed-dim: '#b7c8e1'
  on-secondary-fixed: '#0b1c30'
  on-secondary-fixed-variant: '#38485d'
  tertiary-fixed: '#c5eae1'
  tertiary-fixed-dim: '#aacec6'
  on-tertiary-fixed: '#00201c'
  on-tertiary-fixed-variant: '#2b4d47'
  background: '#fbf9fa'
  on-background: '#1b1b1c'
  surface-variant: '#e4e2e3'
  slate-bg: '#F8FAFC'
  surface-elevated: '#FFFFFF'
  surface-subtle: '#F1F5F9'
  surface-sunken: '#E2E8F0'
  border-soft: '#E2E8F0'
  text-strong: '#1E293B'
  text-muted: '#64748B'
  status-safe: '#4D7C6F'
  status-subtle-amber: '#92704C'
  status-soft-rose: '#9F5858'
typography:
  display:
    fontFamily: Manrope
    fontSize: 34px
    fontWeight: '600'
    lineHeight: 42px
  headline-lg:
    fontFamily: Manrope
    fontSize: 26px
    fontWeight: '600'
    lineHeight: 34px
  headline-md:
    fontFamily: Manrope
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 26px
  headline-sm:
    fontFamily: Manrope
    fontSize: 17px
    fontWeight: '600'
    lineHeight: 22px
  body-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  body-sm:
    fontFamily: Plus Jakarta Sans
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 18px
  label-lg:
    fontFamily: Manrope
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 18px
  label-md:
    fontFamily: Manrope
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
  label-sm:
    fontFamily: Manrope
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 14px
  numeric-keypad:
    fontFamily: Manrope
    fontSize: 24px
    fontWeight: '500'
    lineHeight: 32px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  margin: 1.25rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 0.875rem
  space-lg: 1.25rem
  space-xl: 2rem
---

## Brand & Style

This design system reimagines digital privacy away from aggressive neon alert systems toward an atmosphere of serene, architectural containment. Security should feel peaceful, dependable, and quietly authoritative rather than alarming. The visual direction blends **Warm Minimalist Glass** with **Editorial Precision**, curating a soft slate gray and cool-neutral environment that eliminates visual exhaustion and tension during frequent daily unlocks.

The target audience seeks an unobtrusive, refined utility that integrates effortlessly into premium mobile OS environments. Interfaces breathe with generous padding, delicate 1px satin contours, gentle diffused shadows, and whisper-soft monochromatic surface gradations. Interacting with locked applications and biometric triggers feels tactile, balanced, and reassuring.

## Colors

The palette transitions entirely from high-contrast electric saturation to a calm, low-strain spectrum of cool slates, bone whites, and muted minerals. Color is applied with deliberate restraint to preserve an uncluttered visual field.

### Core Roles
- **Primary (`#4B5563`)**: Deep Slate. Anchors focal action touchpoints, primary unlock states, and verified cryptographic shields with understated weight.
- **Secondary (`#64748B`)**: Cool Slate Gray. Handles structural containers, secondary interactive states, unselected tabs, and ambient icons.
- **Tertiary (`#5A7C75`)**: Muted Sage. Replaces fluorescent green to indicate successful biometric authentication, vault encryption integrity, and active safety states in a soothing, human-centric hue.
- **Neutral Canvas (`#F8FAFC`)**: Soft Porcelain. A low-luminance light background engineered to soften high ambient glare while avoiding the sterile severity of pure `#FFFFFF`.

### Functional Substrates
- **Base Canvas**: `#F8FAFC`
- **Surface Elevation 1 (Cards, Vault Lists)**: `#FFFFFF` with a 1px contour of `#E2E8F0`.
- **Surface Elevation 2 (Floating Modals, Dialers)**: `#FFFFFF` with diffused ambient dispersion.
- **Interactive Depressions & Sliders**: `#F1F5F9`.

### Status & Feedback
- **Secure / Verified**: Muted Sage (`#4D7C6F`), paired with a faint tinted background wash.
- **Attention Required**: Warm Dust Amber (`#92704C`).
- **Access Anomaly / Intruder**: Muted Rosewood (`#9F5858`), ensuring alerts remain distinct without causing visual panic.

## Typography

Typography shifts away from aggressive techno-monospaced fonts toward a harmonious dialogue between modern geometric balance and humanist readability.

- **Manrope** shapes all headlines, metrics, PIN digits, and authentication labels. Its rounded, open letterforms convey an organized, premium calm while maintaining architectural solidity. Tabular numbers (`font-variant-numeric: tabular-nums`) must be active for numeric authentication readouts, lock countdowns, and logs.
- **Plus Jakarta Sans** grounds explanatory dialogs, vault descriptions, device permission lists, and instructional notices. Its soft terminals and open apertures offer seamless reading at small touch-screen scales.
- Uppercase tracking is kept restrained (`0.02em` to `0.04em`) to maintain an approachable, quiet luxury sensibility rather than militaristic telemetry.

## Layout & Spacing

The layout philosophy prioritizes uncluttered airiness, consistent alignment, and stress-free single-handed ergonomics.

### Grid & Canvas Structure
- **Screen Margins**: `1.25rem` (`20px`) on standard viewports, establishing a clean frame around cards and key surfaces.
- **Touch Zone Ergonomics**: In lock and auth views, high-frequency actions (PIN pad cells, biometric trigger pads, pattern canvases) are anchored in the lower 45% of the viewport. Primary status readouts (vault health, app icon preview) sit comfortably in the upper half with uncrowded breathing room.
- **Component Intervals**: Standard vertical cadence follows an 8-point base rhythm, utilizing `space-sm` (`0.5rem`) within card elements, `space-md` (`0.875rem`) between adjacent list rows, and `space-xl` (`2rem`) between distinct vault operational panels.
- **Large Screen & Foldable Constraints**: When horizontal width exceeds `480px`, authentication containers restrict their max-width to `400px` centered, preserving natural thumb travel and visual balance.

## Shapes

The geometric personality features soft, natural curves that project friendliness, warmth, and organic balance.

- **Primary Cards & Containers**: Employ `rounded-lg` (`1rem`) to create smooth framing on rounded smartphone displays.
- **Interactive Buttons & Input Fields**: Styled with `0.75rem` to `1rem` corner radii for a pillowy, accessible feel under finger touch.
- **Numeric Dialer Cells & Biometric Target Shields**: Formed with perfect full circles (`rounded-full`) to offer gentle, centered tactile targets.
- **Badges & Status Pills**: Rendered as smooth pills (`rounded-full`) to differentiate metadata from structural square tiles.

## Components

### Buttons & Interactive Controls
- **Primary Confirm Button**: Solid deep slate fill (`#4B5563`) with clean white typography (`#FFFFFF`). No hard drop shadows; hover and active states shift smoothly to `#334155` with an ease-in-out transition.
- **Secondary / Ghost Button**: Translucent slate container (`#F1F5F9`) with `#475569` text and a subtle `#E2E8F0` border.
- **Tertiary / Subdued Action**: Borderless with `#64748B` typography, deepening to `#1E293B` upon contact.

### Numeric Keypad & PIN Inputs
- **Dialer Cells**: Circular 72px touch cells backed by soft porcelain white (`#FFFFFF`) with a delicate 1px border of `#E2E8F0`. Digits use `Manrope` Medium. On press, cells subtly recess into `#F1F5F9` with quiet haptic response.
- **PIN Dot Indicators**: 10px circular dots. Idle: unfilled `#E2E8F0` stroke. Filled: solid `#4B5563`. Error state: gentle transition to `#9F5858` with horizontal dampening shake.

### Biometric Scan Zone
- Replaces high-intensity laser scans with a calm breathing wave. 
- Gentle concentric rings in semi-transparent `#64748B` (10% and 5% opacity).
- Biometric glyph renders in refined slate (`#4B5563`), transitioning softly to Muted Sage (`#4D7C6F`) on successful match, accompanied by a modest checkmark indicator.

### App Locker Row Items
- **Structure**: Clean list item on `#FFFFFF` with a 40px rounded app icon, application title in `Manrope` SemiBold (`#1E293B`), and category subtext in `#64748B`.
- **Lock Toggle**: Soft pill track. When locked: slate fill (`#4B5563`) with smooth white thumb holding a minimal closed shackle outline. When unlocked: pale gray track (`#E2E8F0`) with white thumb and open shackle icon.

### Vault & Intruder Log Cards
- **Vault Status Container**: Minimalist card with a soft radial tint (`rgba(100, 116, 139, 0.05)`). Features an understated circular SVG progress ring highlighting encrypted percentage in Deep Slate and Muted Sage.
- **Intruder Snapshot Tile**: White card bounded by a soft `#E2E8F0` stroke. Contains an incident tag in Muted Rosewood (`#9F5858`), a gently rounded camera preview (`rounded-md`), and timestamp metrics in clean tabular Manrope.

### Status Badges & Chips
- Compact pill-shaped chips with low-contrast, pastel-tinted substrates:
  - **Protected / Active**: `#ECFDF5` background with `#4D7C6F` text and an optional `#D1FAE5` border.
  - **Vault Locked**: `#F1F5F9` background with `#475569` text.
  - **Warning / Unverified**: `#FFFBEB` background with `#92704C` text.
